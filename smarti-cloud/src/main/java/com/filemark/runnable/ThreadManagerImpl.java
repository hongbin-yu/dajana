package com.filemark.runnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filemark.config.SmartiConfig;
import com.filemark.utils.TaskLock;



/**
 * Manage Roller's thread use.
 */

public abstract class ThreadManagerImpl implements ThreadManager {
    
    private static final Log log = LogFactory.getLog(ThreadManagerImpl.class);
    
    // our own scheduler thread
    private Thread schedulerThread = null;
    
    // a simple thread executor
    private final ExecutorService serviceScheduler;
    
    
    public ThreadManagerImpl() {
        
        log.info("Instantiating Thread Manager");
        
        serviceScheduler = Executors.newCachedThreadPool();
    }
    
    
    public void initialize() throws Exception {
                    
        // initialize tasks, making sure that each task has a tasklock record in the db
        List<SmartiTask> smartiTasks = new ArrayList<SmartiTask>();
        String tasksStr = SmartiConfig.getProperty("tasks.enabled");
        String[] tasks = StringUtils.stripAll(StringUtils.split(tasksStr, ","));
        for ( String taskName : tasks ) {
            
            String taskClassName = SmartiConfig.getProperty("tasks."+taskName+".class");
            if(taskClassName != null) {
                log.info("Initializing task: "+taskName);
                
                try {
                    Class taskClass = Class.forName(taskClassName);
                    SmartiTask task = (SmartiTask) taskClass.newInstance();
                    task.init(taskName);
                    
                    // make sure there is a tasklock record in the db
                    TaskLock taskLock = getTaskLockByName(task.getName());
                    if (taskLock == null) {
                        log.debug("Task record does not exist, inserting empty record to start with");

                        // insert an empty record
                        taskLock = new TaskLock();
                        taskLock.setName(task.getName());
                        taskLock.setLastRun(new Date(0));
                        taskLock.setTimeAquired(new Date(0));
                        taskLock.setTimeLeased(0);

                        // save it
                        this.saveTaskLock(taskLock);
                    }
                    
                    // add it to the list of configured tasks
                    smartiTasks.add(task);
                    
                } catch (ClassCastException ex) {
                    log.warn("Task does not extend RollerTask class", ex);
                } catch (Exception ex) {
                    log.error("Error instantiating task", ex);
                }
            }
        }
        
        // create scheduler
        TaskScheduler scheduler = new TaskScheduler(smartiTasks);
        
        // start scheduler thread, but only if it's not already running
        if (schedulerThread == null && scheduler != null) {
            log.debug("Starting scheduler thread");
            schedulerThread = new Thread(scheduler, "Roller Weblogger Task Scheduler");
            // set thread priority between MAX and NORM so we get slightly preferential treatment
            schedulerThread.setPriority((Thread.MAX_PRIORITY + Thread.NORM_PRIORITY)/2);
            schedulerThread.start();
        }
    }
    
    
    public void executeInBackground(Runnable runnable)
            throws InterruptedException {
        Future task = serviceScheduler.submit(runnable);
    }
    
    
    public void executeInForeground(Runnable runnable)
            throws InterruptedException {
        Future task = serviceScheduler.submit(runnable);
        
        // since this task is really meant to be executed within this calling 
        // thread, here we can add a little code here to loop until it realizes 
        // the task is done
        while(!task.isDone()) {
            Thread.sleep(500);
        }
    }
    
    
    public void shutdown() {
        
        log.debug("starting shutdown sequence");
        
        // trigger an immediate shutdown of any backgrounded tasks
        serviceScheduler.shutdownNow();
        
        // only stop if we are already running
        if(schedulerThread != null) {
            log.debug("Stopping scheduler");
            schedulerThread.interrupt();
        }
    }
    
    
    public void release() {
        // no-op
    }
    
    
    /**
     * Default implementation of lease registration, always returns true.
     * 
     * Subclasses should override this method if they plan to run in an
     * environment that supports clustered deployments.
     */
    public boolean registerLease(SmartiTask task) {
        return true;
    }
    
    
    /**
     * Default implementation of lease unregistration, always returns true.
     * 
     * Subclasses should override this method if they plan to run in an
     * environment that supports clustered deployments.
     */
    public boolean unregisterLease(SmartiTask task) {
        return true;
    }


}
