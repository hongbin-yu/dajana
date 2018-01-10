package com.filemark.jcr.model;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
@Node(jcrMixinTypes = "mix:referenceable")
public class Device implements SmartiNode {

	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title;
	@Field
	protected String name;
	@Field
	private String location;
	@Field
	private String order;
	@Field
	private String status;
	private String usage;
	
	public Device() {
		super();
	}
	
	public Device(String location) {

		super();
		this.location = location;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}

}
