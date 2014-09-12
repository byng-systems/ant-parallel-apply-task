/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.byng.antparallelapply.taskdefs.optional;

import java.lang.reflect.Field;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.apache.tools.ant.Project;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author M.D.Ward <matthew.ward@byng-systems.com>
 */
public class ParallelApplyTest {
    
    @Test
    public void testConstructorCallsSetParallel() {
        ParallelApply task = new ParallelApply();
        
        try {
            Field field = task.getClass().getSuperclass().getDeclaredField("parallel");
            field.setAccessible(true);
            
            assertTrue(field.getBoolean(task));
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            fail("Unable to access field and/or field value for 'parallel'");
        }
    }
    
    @Test
    public void testSetParallelIgnoresGivenValue() {
        
        ParallelApply task = new ParallelApply();
        
        try {
            Field field = task.getClass().getSuperclass().getDeclaredField("parallel");
            field.setAccessible(true);
            
            for (boolean parallel : new boolean[]{true, false}) {
                task.setParallel(parallel);
                assertTrue(field.getBoolean(task));
            }
            
            task.setParallel(true);
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            fail("Unable to access field and/or field value for 'parallel'");
        }
    }
    
    public void testThreadCountGetterSetterMethods() {
        
        ParallelApply task = new ParallelApply();
        
        try {
            
            Field field = task.getClass().getDeclaredField("threadCount");
            field.setAccessible(true);
            
            for (int value : new int[]{2, 4, 8, 16, 32, 64}) {
                task.setThreadCount(value);
                assertEquals(value, task.getThreadCount());
                assertEquals(value, field.getInt(task));
            }
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            fail("Unable to access field and/or field value for 'threadCount'");
        }
        
    }
    
    @Test
    public void testLogThresholdGetterSetterMethods() {
        ParallelApply task = new ParallelApply();
        
        try {
            Field field = task.getClass().getDeclaredField("logThreshold");
            field.setAccessible(true);
            
            for (int value : new int[]{Project.MSG_ERR, Project.MSG_WARN, Project.MSG_INFO, Project.MSG_VERBOSE, Project.MSG_VERBOSE}) {
                task.setLogThreshold(value);
                assertEquals(value, task.getLogThreshold());
                assertEquals(value, field.getInt(task));
            }
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            fail("Unable to access field and/or field value for 'logThreshold'");
        }
        
    }
    
    @Test
    public void testLogThresholdSetterRejectsIllegalValues() {
        ParallelApply task = new ParallelApply();
        
        for (int i = 5; i <= 10; i++) {
            try {
                task.setLogThreshold(i);
                fail("An IllegalArgumentException should have been thrown with out of bounds value " + i);
            } catch (Throwable t) {
                assertTrue(t instanceof IllegalArgumentException);
            }
        }

        for (int i = -1; i >= -5; i--) {
            try {
                task.setLogThreshold(i);
                fail("An IllegalArgumentException should have been thrown with out of bounds value " + i);
            } catch (Throwable t) {
                assertTrue(t instanceof IllegalArgumentException);
            }
        }
    }
    
    @Test
    public void testGetNameReturnsConstantValue() {
        assertEquals("parallel-apply", new ParallelApply().getTaskName());
    }
    
    
}
