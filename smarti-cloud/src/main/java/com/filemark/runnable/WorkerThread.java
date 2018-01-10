package com.filemark.runnable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * A generic worker thread that knows how execute a Job.
 */
public class WorkerThread extends Thread {
    
    private static Log log = LogFactory.getLog(WorkerThread.class);
    
    String id = null;
    Job job = null;
    
    
    /**
     * A simple worker.
     */
    public WorkerThread(String id) {
        super(id);
        this.id = id;
    }
    
    
    /**
     * Start off with a job to do.
     */
    public WorkerThread(String id, Job job) {
        super(id);
        this.id = id;
        this.job = job;
    }
    
    
    /**
     * Thread execution.
     *
     * We just execute the job we were given if it's non-null.
     */
    public void run() {
        
        // we only run once
        if (this.job != null) {
            // process job
            try {
                this.job.execute();
            } catch(Throwable t) {
                // oops
                log.error("Error executing job. "+
                        "Worker = "+this.id+", "+
                        "Job = "+this.job.getClass().getName(), t);
            }
            
            // since this is a thread we have to make sure that we tidy up ourselves
            //Weblogger roller = WebloggerFactory.getWeblogger();
            //roller.release();
        }
        
    }
    
    
    /**
     * Set the job for this worker.
     */
    public void setJob(Job newJob) {
        log.debug("NEW JOB: "+newJob.getClass().getName());
        
        // set the job
        this.job = newJob;
    }
    
}
