package com.filemark.jcr.serviceImpl;

import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filemark.jcr.service.AssetOperation;
import com.filemark.jcr.service.JcrServices;
import com.filemark.search.operation.IndexOperation;

public class CreateFolderIconOperation extends AssetOperation {
    private static Log mLogger = LogFactory.getFactory().getInstance(IndexOperation.class);

	private String path;
	public CreateFolderIconOperation(JcrServices jcrService, String path) {
		super(jcrService);
		this.path = path;
	}

	@Override
	protected void doRun() {
		try {
			jcrService.updateFolderIcon(path);
		} catch (RepositoryException e) {
			mLogger.error(e.getMessage());
		}

	}

}
