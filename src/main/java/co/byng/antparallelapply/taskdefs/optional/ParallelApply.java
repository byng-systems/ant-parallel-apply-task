/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.byng.antparallelapply.taskdefs.optional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteOn;



/**
 * Executes a given command in parallel, supplying a set of files as arguments.
 *
 * @ant.task category="control" name="parallel-apply"
 */
public class ParallelApply extends ExecuteOn {

    /**
     * Indicates the default number of threads
     */
    protected static final int DEFAULT_THREADS = 4;
    
    /**
     * 
     */
    protected String executable;

    /**
     * 
     */
    private int threadCount = ParallelApply.DEFAULT_THREADS;
    
    /**
     * 
     */
    private int logThreshold = Project.MSG_INFO;
    
    /**
     * File message buffer
     */
    private final Map<File, List<BufferedLogMessage>> fileMessageBuffer = new LinkedHashMap<>();
    
    /**
     * List of exceptions that are thrown
     */
    private final Map<File, Throwable> subtaskExceptions = new LinkedHashMap<>();
    
    
    
    /**
     * Constructor - creates a new instance of ParallelApply
     */
    public ParallelApply() {
        super.setParallel(true);
    }
    
    /**
     * Sets the 'parallel' property; not applicable to this implementation which
     * operates in parallel by design; therefore this method has no effect and
     * is deprecated.
     * 
     * @param parallel
     *      (Ignored) New boolean of 'parallel' property
     * @deprecated parallel-apply task implicitly carries out the apply operation
     * in parallel
     */
    @Override
    public void setParallel(boolean parallel) {
        super.setParallel(true);
    }
    
    /**
     * 
     * 
     * @return 
     */
    public int getThreadCount() {
        return this.threadCount;
    }

    /**
     * 
     * @param threadCount 
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * 
     * @return 
     */
    public String getExecutable() {
        return this.executable;
    }
    
    /**
     * 
     * @param value 
     */
    @Override
    public void setExecutable(String value) {
        super.setExecutable(value);
        this.executable = value;
    }

    /**
     * 
     * @return 
     */
    public int getLogThreshold() {
        return logThreshold;
    }
    
    /**
     * 
     * @param logThreshold 
     * @throws IllegalArgumentException
     */
    public void setLogThreshold(int logThreshold) {
        if (logThreshold < Project.MSG_ERR || logThreshold > Project.MSG_VERBOSE) {
            throw new IllegalArgumentException("Log level threshold must be given as one of the ANT message logging levels");
        }
        
        this.logThreshold = logThreshold;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getTaskName() {
        return "parallel-apply";
    }

    /**
     * 
     * @param file
     * @return 
     */
    private ParallelApplySubtask createSubtask(File file) {
        
        List<BufferedLogMessage> buffer = new ArrayList<>();
        this.fileMessageBuffer.put(file, buffer);
        
        return new ParallelApplySubtask(
            file,
            this.getExecutable(),
            this.cmdl.getArguments(),
            buffer,
            this.getProject()
        );
    }

    /**
     * 
     * @param exe
     * @param fileNames
     * @param baseDirs
     * @throws IOException
     * @throws BuildException 
     */
    @Override
    protected void runParallel(Execute exe, Vector fileNames, Vector baseDirs) throws IOException, BuildException {

        this.fileMessageBuffer.clear();
        this.subtaskExceptions.clear();
        
        super.log("Executing command '" + this.cmdl.toString() + "' on " + fileNames.size() + " files");
        
        ExecutorService threadPool = Executors.newFixedThreadPool(this.threadCount);

        for (int i = 0; i < fileNames.size(); i++) {
            final File file = new File((File) baseDirs.get(i), (String) fileNames.get(i));

            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    ParallelApplySubtask task = createSubtask(file);
                    
                    try {
                        task.execute();
                    } catch (BuildException ex) {
                        failTask(task, ex);
                    }
                }

            });
        }
        
        try {
            threadPool.shutdown();
            while (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {}
            
            dumpMessages();
            
            Thread.sleep(1000);
            
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
        
        if (this.subtaskExceptions.size() > 0) {
            throw new ParallelBuildException(
                "Some subtasks throw exceptions during processing",
                this.subtaskExceptions.values()
            );
        }
    }

    protected void failTask(ParallelApplySubtask task, Throwable t) {
        
        this.subtaskExceptions.put(task.getFile() ,t);
    }

    protected void dumpMessages() throws BuildException {
        
        for (File f : fileMessageBuffer.keySet()) {
            
            List<BufferedLogMessage> messageBuffer = fileMessageBuffer.get(f);
            
            if (messageBuffer == null) {
                continue;
            }

            for (BufferedLogMessage m : messageBuffer) {
                int messageLogLevel = m.getMessageLogLevel();

                if (messageLogLevel <= this.logThreshold) {
                   super.log(m.getMessage(), messageLogLevel);
                }
            }

        }
        
        if (subtaskExceptions.size() > 0) {
            
            super.log("\n\nErrors were encountered with the following files:\n", Project.MSG_ERR);
            for (File f : subtaskExceptions.keySet()) {
                super.log(f.getPath() + ":", Project.MSG_ERR);
                
                for (BufferedLogMessage m : fileMessageBuffer.get(f)) {
                    super.log("\t" + m.getMessage().replace("\n", "\n\t"), Project.MSG_ERR);
                }
                
                super.log("\n\n", Project.MSG_INFO);
            }
        } else {
            super.log("All subtasks executed successfully.");
        }
        
    }
    
}
