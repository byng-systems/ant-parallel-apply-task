/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.byng.antparallelapply.taskdefs.optional;

import java.io.Serializable;

/**
 *
 * @author M.D.Ward <matthew.ward@byng-systems.com>
 */
public class BufferedLogMessage implements Serializable {
    
    private String message;
    private Throwable throwable;
    private int messageLogLevel;

    public BufferedLogMessage(String message, Throwable throwable, int messageLogLevel) {
        setMessage(message);
        setThrowable(throwable);
        setMessageLogLevel(messageLogLevel);
    }

    public BufferedLogMessage() {
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean hasMessage() {
        return (this.message != null);
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public boolean hasThrowable() {
        return (this.throwable != null);
    }

    public int getMessageLogLevel() {
        return messageLogLevel;
    }

    public void setMessageLogLevel(int messageLogLevel) {
        this.messageLogLevel = messageLogLevel;
    }
    
}
