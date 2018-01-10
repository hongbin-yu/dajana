
package com.filemark.runnable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * An abstract class representing a scheduled task in Roller that will always
 * attempt to acquire a lease before doing its work.
 */
public abstract class SmartiTaskWithLeasing extends SmartiTask {
    private static Log log = LogFactory.getLog(SmartiTaskWithLeasing.class);
    
    @Autowired
    private ThreadManager threadManager;
    /**
     * Run the task.
     */
    public abstract void runTask() throws Exception;
    
    
    /**
     * The run() method as called by our thread manager.
     *
     * This method is purposely defined as "final" so that any tasks that are
     * defined may not override it and remove any of its functionality.  It is
     * setup to provide some basic functionality to the running of all tasks,
     * such as lease acquisition and releasing.
     *
     * Roller tasks should put their logic in the runTask() method.
     */
    public final void run() {
        
        
        boolean lockAcquired = false;
        try {
            log.debug(getName()+": Attempting to acquire lease");
            
            lockAcquired = threadManager.registerLease(this);
            
            // now if we have a lock then run the task
            if(lockAcquired) {
                log.debug(getName()+": Lease acquired, running task");
                this.runTask();
            } else {
                log.debug(getName()+": Lease NOT acquired, cannot continue");
                return;
            }
            
        } catch (Exception ex) {
            log.error(getName()+": Unexpected exception", ex);
        } finally {
            
            if(lockAcquired) {
                
                log.debug(getName()+": Attempting to release lease");
                
                boolean lockReleased = threadManager.unregisterLease(this);
                
                if(lockReleased) {
                    log.debug(getName()+": Lease released, task finished");
                } else {
                    log.debug(getName()+": Lease NOT released, some kind of problem");
                }
            }
            
            // always release Roller session
            threadManager.release();
        }
        
    }
    
}
