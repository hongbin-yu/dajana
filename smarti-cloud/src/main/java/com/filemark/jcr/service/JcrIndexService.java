package com.filemark.jcr.service;


public interface JcrIndexService {

	public void runScheduledQueue();
	public String getClusterId();
}
