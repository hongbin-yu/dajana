package com.filemark.jcr.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Log implements SmartiNode{

	
	@Field
	private String event;
	@Field
	private String username;
	@Field
	private String content;
	@Field(path=true)
	private String path;
	@Field
	private Date createdDate = new Date();

	public Log(){
		
	}
	
	public Log(String username, String message, String event) {
		this.username = username;
		this.content = message;
		this.event = event;
	}
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getPath() {
		if(path==null && event!=null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
			path = Folder.root+"/logs/"+sf.format(createdDate).replaceAll("-", "_")+"/"+event.toLowerCase().replaceFirst(" ", "_")+"/"+username.toLowerCase()+"/log";

			
		}
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getType() {
		return Log.class.getSimpleName().toLowerCase();
	}
}
