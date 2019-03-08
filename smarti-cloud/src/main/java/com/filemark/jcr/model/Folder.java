package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jcr.Property;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.codehaus.jackson.annotate.JsonIgnore;

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
	private String description;	
	@Field 
	private String groups;
	@Field 
	private String orderby;
	@Field
	private String createdBy;
	@Field 
	private Date lastModified;
	@Field 
	private Date modifiedDate;	
	@Field
	protected String passcode;	
	@Field
	protected String readonly;
	@Field
	private String sharing;
	@Field
	protected String intranet;	
	@Field
	protected String notice;		
	@Field
	protected String resolution;		
	private Date lastUpdated;	
	private String limit;
	private int level;
	private long childCount;

	private String ocm_classname;
	private String nodeName;
	@JsonIgnore
	private boolean hasNodes;
	//private List<Property> properties;
	public static String root="";	
	private String parent;
	private String parentTitle;	
	private List<Folder> subfolders;
	private List<Asset> assets;
	private List<JcrNode> breadcrum;
	
	
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
	
	
	public String getParentTitle() {
		return parentTitle;
	}

	public void setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
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

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<Folder> getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(List<Folder> subfolders) {
		this.subfolders = subfolders;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<JcrNode> getBreadcrum() {
		return breadcrum;
	}

	public void setBreadcrum(List<JcrNode> breadcrum) {
		this.breadcrum = breadcrum;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}


	
	
}
