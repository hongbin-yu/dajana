package com.filemark.jcr.model;

import java.util.Date;
import java.util.List;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:referenceable")
public class Email {
	private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title;
	@Field
	protected String name;
	@Field
	protected String content;
	@Field
	protected String from;	
	@Field
	protected String to;		
	@Field
	protected String cc;		
	@Field
	private String subject;
	@Field
	private String flags;	
	@Field
	private Date received;
	
	@Collection
	private List<Asset> attached;
	public Email() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Email(String title, String name, String from, String to, String cc,
			String subject) {
		super();
		this.title = title;
		this.name = name;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
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
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<Asset> getAttached() {
		return attached;
	}
	public void setAttached(List<Asset> attached) {
		this.attached = attached;
	}
	public Date getReceived() {
		return received;
	}
	public void setReceived(Date received) {
		this.received = received;
	}
	
	
}
