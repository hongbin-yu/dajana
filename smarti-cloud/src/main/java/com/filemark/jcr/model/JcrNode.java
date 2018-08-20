package com.filemark.jcr.model;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

public class JcrNode implements SmartiNode {
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title;
	
	public JcrNode() {
		super();
	}
	
	
	public JcrNode(String path, String title) {
		super();
		this.path = path;
		this.title = title;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
