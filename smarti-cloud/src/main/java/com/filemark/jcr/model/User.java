package com.filemark.jcr.model;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import com.filemark.jcr.model.SmartiNode;
import com.filemark.sso.JwtUtil;
import com.google.gson.JsonObject;

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
	@Field 
	private Date lastDydns;
	@Field
	private String role;
	@Field
	private String code;	
	@Field
	private String xmppid;	
	@Field
	private String status;		
	@Field 
	private Date lastVerified;		
	@Field
	private String icon="/resources/images/usericon.png";	
	@Field
	private String city;	
	@Field
	private byte[] avatar;
	@Field	
	private String organization;
	@Field
	private String location;
	
	@Collection(autoUpdate=false)
	private Set<Role> roles = new HashSet<Role>();
	
	String redirect=null;
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

	public Date getLastDydns() {
		return lastDydns;
	}

	public void setLastDydns(Date lastDydns) {
		this.lastDydns = lastDydns;
	}

	public String getRole() {

		if(role == null) {
			if("home".equals(userName)) return "Owner";
			if("templates".equals(userName)) return "Administrator";
			return "User";		
		}
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getEncodedJson() {
		String encodedJson ="";

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", userName);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("localIp", getLocalIp());
		jsonObject.addProperty("signingKey", signingKey);
		jsonObject.addProperty("rediret", redirect);
		jsonObject.addProperty("expired",lastUpdated==null?0:lastUpdated.getTime());
		encodedJson = jsonObject.toString();
		
		return JwtUtil.encode(encodedJson);
	}
	
	public String getIcon() {
			if (icon==null)
				return "/protected/file/icon.jpg?path="+"/assets/"+userName+"/icon/x48.jpg";
			else return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getLastVerified() {
		return lastVerified;
	}

	public void setLastVerified(Date lastVerified) {
		this.lastVerified = lastVerified;
	}

	public String getXmppid() {
		return xmppid;
	}

	public void setXmppid(String xmppid) {
		this.xmppid = xmppid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String getType() {
		return User.class.getSimpleName().toLowerCase();
	}
}
