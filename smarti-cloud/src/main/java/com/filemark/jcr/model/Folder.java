package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jcr.Property;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:versionable")
public class Folder implements SmartiNode, Serializable {

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
	protected String name;
	@Field 
	private long rank;
	@Field 
	private String location;
	@Field 
	private String groups;
	@Field 
	private String orderby;
	@Field
	private String createdBy;
	@Field 
	private Date lastModified;
	@Field
	protected String passcode;	
	@Field
	protected String readonly;
	@Field
	private String sharing;
	@Field
	protected String intranet;		
	private Date lastUpdated;	
	private String limit;
	private int level;
	private long childCount;

	private String ocm_classname;
	private String nodeName;
	private boolean hasNodes;
	//private List<Property> properties;
	public static String root="";	
	private String parent;

	
	public Folder() {
		super();

	}	
	
	public Folder(String path, String title, int rank) {
		super();
		this.path = path;
		this.title = title;
		this.rank = rank;
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
		if(title==null || title.equals(""))
			return name;
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
	public boolean isHasNodes() {
		return hasNodes;
	}
	public void setHasNodes(boolean hasNodes) {
		this.hasNodes = hasNodes;
	}
	
	
	public boolean getHasNodes() {
		return hasNodes;
	}
/*	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}*/

	public String getOcm_classname() {
		return ocm_classname;
	}
	public void setOcm_classname(String ocm_classname) {
		this.ocm_classname = ocm_classname;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public long getRank() {
		return rank;
	}
	public void setRank(long rank2) {
		this.rank = rank2;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public long getChildCount() {
		return childCount;
	}
	public void setChildCount(long childCount) {
		this.childCount = childCount;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String[] getRoles() {
		if(groups !=null) {
			return groups.split(" ");
		}
		return null;
	}
	
	public void setRoles (String [] roles) {
		if(roles!=null) {
			groups = "";
			for(String role:roles) {
				groups+=role+" ";
			}
			groups = groups.trim();
		}
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getIntranet() {
		return intranet;
	}

	public void setIntranet(String intranet) {
		this.intranet = intranet;
	}
	
}