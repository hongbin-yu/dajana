package com.filemark.runnable;

import com.filemark.utils.TaskLock;



/**
 * Thread management for executing scheduled and asynchronous tasks.
 */
public interface ThreadManager {
    
    public static final long MIN_RATE_INTERVAL_MINS = 1;
    
    
    /**
     * Initialize the thread management system.
     *
     * @throws InitializationException If there is a problem during initialization.
     */
    public void initialize() throws Exception;
    
    
    /**
     * Execute runnable in background (asynchronously).
     * @param runnable
     * @throws java.lang.InterruptedException
     */
    public void executeInBackground(Runnable runnable)
        throws InterruptedException;
    
    
    /**
     * Execute runnable in foreground (synchronously).
     */
    public void executeInForeground(Runnable runnable)
        throws InterruptedException;
    
    
    /**
     * Lookup a TaskLock by name.
     * 
     * @param name The name of the task.
     * @return The TaskLock for the task, or null if not found.
     * @throws WebloggerException If there is an error looking up the TaskLock.
     */
    public TaskLock getTaskLockByName(String name) throws Exception;

    
    /**
     * Save a TaskLock.
     * 
     * @param tasklock The TaskLock to save.
     * @throws WebloggerException If there is an error saving the TaskLock.
     */
    public void saveTaskLock(TaskLock tasklock) throws Exception;


    /**
     * Try to register a lease for a given RollerTask.
     *
     * @param task The RollerTask to register the lease for.
     * @return boolean True if lease was registered, False otherwise.
     */
    public boolean registerLease(SmartiTask task);
    
    
    /**
     * Try to unregister the lease for a given RollerTask.
     *
     * @param task The RollerTask to unregister the lease for.
     * @return boolean True if lease was unregistered (or was not leased), False otherwise.
     */
    public boolean unregisterLease(SmartiTask task);
    
    
    /**
     * Shutdown.
     */
    public void shutdown();
    
    
    /**
     * Release all resources associated with Roller session.
     */
    public void release();
    
}
