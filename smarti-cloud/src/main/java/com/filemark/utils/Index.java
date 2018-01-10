package com.filemark.utils;

public class Index {

	private long id;
	private String label;
	private String name;
	
	
	
	public Index(long id, String label, String name) {
		super();
		this.id = id;
		this.label = label;
		this.name = name;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
