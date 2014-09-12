/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.byng.antparallelapply.taskdefs.optional;

import java.io.File;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;

/**
 * 
 * @author M.D.Ward <matthew.ward@byng-systems.com>
 */
class ParallelApplySubtask extends ExecTask {

    private final File file;
    private final List<BufferedLogMessage> buffer;

    public ParallelApplySubtask(
        final File file,
        String executable,
        String[] commandLineArguments,
        List<BufferedLogMessage> buffer,
        Project project
    ) {

        this.file = file;
        this.buffer = buffer;

        setFailonerror(true);

        setExecutable(executable);
        this.cmdl.addArguments(commandLineArguments);
        this.cmdl.addArguments(new String[] {file.getAbsolutePath()});

        this.setProject(project);
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public void log(String msg, Throwable t, int msgLevel) {
        this.buffer.add(new BufferedLogMessage(msg, t, msgLevel));
    }

    @Override
    public void log(String msg, int msgLevel) {
        log(msg, null, msgLevel);
    }

    @Override
    public void log(Throwable t, int msgLevel) {
        log(null, t, msgLevel);
    }

}
