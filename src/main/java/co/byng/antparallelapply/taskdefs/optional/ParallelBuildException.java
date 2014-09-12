/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.byng.antparallelapply.taskdefs.optional;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author M.D.Ward <matthew.ward@byng-systems.com>
 */
public class ParallelBuildException extends BuildException {

    private List<Throwable> nestedExceptions;
    
    public ParallelBuildException(String message) {
        super(message);
        
        this.nestedExceptions = new ArrayList<>();
    }
    
    public ParallelBuildException(String message, Throwable[] nestedExceptions) {
        this(message);
        
        for (Throwable t : nestedExceptions) {
            this.addException(t);
        }
    }
    
    public ParallelBuildException(String message, Collection<Throwable> nestedExceptions) {
        this(message);
        
        this.nestedExceptions.addAll(nestedExceptions);
    }
    
    public Throwable[] getExceptions() {
        return this.nestedExceptions.toArray(
            new Throwable[this.nestedExceptions.size()]
        );
    }
    
    public void addException(Throwable t) {
        this.nestedExceptions.add(t);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        
        s.println();
        
        for (Throwable t : this.nestedExceptions) {
            t.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        
        s.write("\n");
        
        for (Throwable t : this.nestedExceptions) {
            t.printStackTrace(s);
        }
    }
    
}
