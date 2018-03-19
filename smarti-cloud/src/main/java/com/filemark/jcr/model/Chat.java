package com.filemark.jcr.model;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:referenceable")
public class Chat implements SmartiNode, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title;
	@Field
	private String content;
	@Field
	private String from;
	@Field
	private String createdBy;
	@Field(jcrName="jcr:lastModified") 
	private Calendar lastModified;
	@Field(jcrName="lastRead") 
	private Date lastRead;
	
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getIcon() {
		if(new File("/mnt/device/"+createdBy+"/assets/icon/x48.jpg").exists()) {
			return "file/icon.jpg?path=/"+createdBy+"/assets/icon/x48.jpg";
		}
		return "/resources/images/usericon.png";
	}

	public String getIconlg() {
		if(new File("/mnt/device/"+createdBy+"/assets/icon/x120.jpg").exists()) {
			return "/file/icon.jpg?path=/"+createdBy+"/assets/icon/x120.jpg";
		}
		return "/resources/images/usericon.png";
	}
	
	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}

	public Date getLastRead() {
		return lastRead;
	}

	public void setLastRead(Date lastRead) {
		this.lastRead = lastRead;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLastPublished() {
		if(this.lastModified == null) return "";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(this.lastModified.getTime());
	}
	
	public String getSubjects() {
		if("0".equals(from)) return "<img alt=\"\" src=\"/smarti-cloud/resources/images/thumbup.png\">";
		if("2".equals(from)) return "<img alt=\"\" src=\"/smarti-cloud/resources/images/thumbdown.png\">";
		return "";
	}
}