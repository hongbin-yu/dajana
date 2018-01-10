package com.filemark.jcr.serviceImpl;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filemark.jcr.service.AssetManager;
import com.filemark.jcr.service.AssetOperation;
import com.filemark.jcr.service.JcrServices;
import com.filemark.runnable.ThreadManager;
import com.filemark.search.IndexManagerImpl;
import com.filemark.search.operation.IndexOperation;
import com.filemark.utils.SmartiConstant;

public class AssetManagerImpl implements AssetManager {
	@Inject
    private ThreadManager assetThreadManager;
    @Inject
	private JcrServices jcrServiceImpl; 
    
    static Log mLogger = LogFactory.getFactory().getInstance(
    		AssetManagerImpl.class);
    


	@Override
	public void executeAssetOperationBackend(AssetOperation op) {
		
		scheduleAssetOperation(op);
	}

	
    @Override
	public void executeCreateFileOperationBackend(String path, Integer w) {
    	CreateFileOperation op = new CreateFileOperation(jcrServiceImpl,path,w);
    	scheduleAssetOperation(op);
		
	}


	@Override
	public void executeCreateFolderIconOperationBackend(String path) {
		CreateFolderIconOperation op = new CreateFolderIconOperation(jcrServiceImpl,path);
		scheduleAssetOperation(op);
		
	}
	
	@Override
	public void executeCreateFolderIconOperationFrontend(String path) {
		CreateFolderIconOperation op = new CreateFolderIconOperation(jcrServiceImpl,path);
		executeAssetOperationNow(op);
	}
	
	@Override
	public void executeImportFolderOperationBackend(String folderPath,String path,String userName,String override) {
		ImportFolderOperation op = new ImportFolderOperation(jcrServiceImpl,folderPath,path,userName,override);
		scheduleAssetOperation(op);
		
	}	

	private void scheduleAssetOperation(final Runnable op) {
        try {

                mLogger.debug("Starting scheduled index operation: "+op.getClass().getName());
                assetThreadManager.executeInBackground(op);
        } catch (InterruptedException e) {
        	mLogger.error("Error executing operation", e);
        }
    }
    
    private void scheduleAssetOperation(final AssetOperation op) {
        try {
             mLogger.debug("Starting scheduled asset operation: "+op.getClass().getName());
             assetThreadManager.executeInBackground(op);
        } catch (InterruptedException e) {
            mLogger.error("Error executing operation", e);
        }
    }
    
    /**
     * @param search
     */
    public void executeAssetOperationNow(final AssetOperation op) {
        try {

                mLogger.debug("Executing index operation now: "+op.getClass().getName());
                assetThreadManager.executeInForeground(op);

        } catch (InterruptedException e) {
            mLogger.error("Error executing operation", e);
        }
    }	
}
