package com.filemark.jcr.model;

import java.util.Date;

public class Analysis {
	private String url;
	private long views;
	private Date lastView;
	public Analysis(String url, long views, Date lastView) {
		super();
		this.url = url;
		this.views = views;
		this.lastView = lastView;
	}
	
	public Analysis() {
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
	}

	public Date getLastView() {
		return lastView;
	}

	public void setLastView(Date lastView) {
		this.lastView = lastView;
	}
	
	public void updateView() {
		views++;
	}
}
