package com.filemark.jcr.service;


public abstract class AssetOperation implements Runnable {

    protected JcrServices jcrService;
    
    public AssetOperation (JcrServices jcrService) {
    	this.jcrService = jcrService;
    }
    public void run() {
        doRun();
    }

    protected abstract void doRun();
    

}
