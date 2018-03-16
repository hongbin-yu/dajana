package com.filemark.jcr.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import com.filemark.jcr.model.SmartiNode;

@Node(jcrMixinTypes = "mix:referenceable")
public class User implements SmartiNode {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field
	private String userName;
	@Field
	private String title;	
	@Field
	private String password;
	@Field
	private String email;
	@Field
	private String phoneNumber;
	@Field
	private String host;	
	@Field
	private String hostIp;	
	@Field
	private String localIp;	
	@Field
	private String port;	
	@Field(path=true)
	private String path;
	@Field
	private String lastIp;
	@Field
	private String signingKey = "dajanaSigningKey";
	@Field 
	private Date lastModified;
	@Field 
	private Date lastUpdated;	
	@Collection(autoUpdate=false)
	private Set<Role> roles = new HashSet<Role>();
	
	public User() {
		
	}
	
	public User(String id, String username, String password, String email,
			String phoneNumber, Set<Role> roles) {
		super();
		this.uid = id;
		this.userName = username;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.roles = roles;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}



	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return this.userName;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSigningKey() {
		return signingKey;
	}

	public void setSigningKey(String signingKey) {
		this.signingKey = signingKey;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
}
