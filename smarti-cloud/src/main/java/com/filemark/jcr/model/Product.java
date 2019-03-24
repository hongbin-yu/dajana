package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:versionable")
public class Product implements Serializable, SmartiNode {

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
	private String description;	
	@Field 
	private Date lastModified;
	@Field
	private float price;
	@Override
	public String getPath() {
		return null;
	}
	@Override
	public String getTitle() {
		
		return null;
	}
	@Override
	public String getUid() {
		return uid;
	}
	
	@Override
	public String getType() {
		return Product.class.getSimpleName().toLowerCase();
	}	
}
