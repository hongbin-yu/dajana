package com.filemark.jcr.serviceImpl;

import com.filemark.jcr.service.AssetOperation;
import com.filemark.jcr.service.JcrServices;

public class CreateFileOperation extends AssetOperation {

	private String path;
	private Integer w;
	public CreateFileOperation(JcrServices jcrService, String path, Integer w) {
		super(jcrService);
		this.path  = path;
		this.w = w;
	}

	@Override
	protected void doRun() {
		jcrService.createFile(path, w);

	}

}
