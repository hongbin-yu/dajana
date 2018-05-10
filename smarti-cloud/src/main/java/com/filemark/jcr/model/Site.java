package com.filemark.jcr.model;

import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import com.filemark.sso.JwtUtil;
import com.google.gson.JsonObject;

@Node(jcrMixinTypes = "mix:referenceable")
public class Site implements SmartiNode {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field
	private String siteName;
	@Field
	private String title;	
	@Field
	private String type;		
	@Field
	private String password;
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
	private String icon="/resources/images/web-icon.png";	
	@Field
	private String city;	

	public Site() {
		
	}
	
	public Site(String id, String sitename, String password, String phoneNumber) {
		super();
		this.uid = id;
		this.siteName = sitename;
		this.password = password;
		this.phoneNumber = phoneNumber;

	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((siteName == null) ? 0 : siteName.hashCode());
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
		Site other = (Site) obj;
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		return true;
	}

	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setsiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
		return this.siteName;
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
			if("home".equals(siteName)) return "Owner";
			if("templates".equals(siteName)) return "Administrator";
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
		jsonObject.addProperty("siteName", siteName);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("localIp", getLocalIp());
		jsonObject.addProperty("signingKey", signingKey);
		jsonObject.addProperty("expired",lastUpdated==null?0:lastUpdated.getTime());
		encodedJson = jsonObject.toString();
		
		return JwtUtil.encode(encodedJson);
	}
	
	public String getIcon() {
			if (icon==null)
				return "/protected/file/icon.jpg?path="+"/"+siteName+"/assets/icon/x48.jpg";
			return icon;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
