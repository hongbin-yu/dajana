package com.filemark.jcr.model;
import java.io.Serializable;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:versionable")
public class News implements Serializable, SmartiNode {

	private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title="";
	@Field
	protected String name;
	@Field
	protected String alt;
	@Field
	protected String url="";
	@Field
	protected String description="";		
	@Field
	protected String subjects="";		
	@Field 
	private String lastPublished="";	
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getUrl() {
		if(url==null) return "";
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		if(description==null) return "";
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubjects() {
		if(subjects==null) return "";
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getLastPublished() {
		if(lastPublished==null) return "";
		return lastPublished;
	}

	public void setLastPublished(String lastPublished) {
		this.lastPublished = lastPublished;
	}

	public void setPath(String path) {

		this.path = path;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
