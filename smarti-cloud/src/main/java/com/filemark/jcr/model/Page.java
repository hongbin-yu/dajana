package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:versionable")
public class Page implements SmartiNode, Serializable{
	
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
	protected String navTitle;	
	@Field
	protected String hideNav;	
	@Field
	protected String showLeftmenu;	
	@Field
	protected String showThememenu;	
	@Field
	protected String showFilter;	
	@Field
	protected String showChat;	
	@Field
	protected String showComment;	
	@Field
	protected String passcode;	
	@Field
	protected String secured;
	@Field
	protected String intranet;	
	@Field
	protected String timeout;		
	@Field
	private String description;
	@Field
	private String keywords;
	@Field
	private String breadcrumb;
	@Field
	private String redirectTo;
	@Field
	private String content;
	@Field
	private String published;
	@Field
	private String createdBy;
	@Field(jcrName="jcr:lastModified") 
	private Date lastModified;
	@Field 
	private Date lastPublished;	
	@Field
	private String status="u";
	@Field
	private String order="zzz";	
	private long depth;
	private boolean hasNodes;
	private boolean showChildren = false;	
	private String parent;
	private String cssClass="";
	private String navigation;
	private String menuPath;	
	private List<Page> childPages;
	
	public String getTitle() {
		return title;
	}
	
	public String getPath() {
		return path;
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

	public String getNavTitle() {
		return navTitle;
	}

	public void setNavTitle(String navTitle) {
		this.navTitle = navTitle;
	}

	public String getHideNav() {
		return hideNav;
	}

	public void setHideNav(String hideNav) {
		this.hideNav = hideNav;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Date getLastPublished() {
		return lastPublished;
	}

	public void setLastPublished(Date lastPublished) {
		this.lastPublished = lastPublished;
	}

	public List<Page> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<Page> childPages) {
		this.childPages = childPages;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHasNodes() {
		return hasNodes;
	}

	public void setHasNodes(boolean hasNodes) {
		this.hasNodes = hasNodes;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isShowChildren() {
		return showChildren;
	}

	public void setShowChildren(boolean showChildren) {
		this.showChildren = showChildren;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getShowLeftmenu() {
		return showLeftmenu;
	}

	public void setShowLeftmenu(String showLeftmenu) {
		this.showLeftmenu = showLeftmenu;
	}

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public long getDepth() {
		return depth;
	}

	public void setDepth(long depth) {
		this.depth = depth;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getShowChat() {
		return showChat;
	}

	public void setShowChat(String showChat) {
		this.showChat = showChat;
	}

	public String getShowComment() {
		return showComment;
	}

	public void setShowComment(String showComment) {
		this.showComment = showComment;
	}

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public String getShowThememenu() {
		return showThememenu;
	}

	public void setShowThememenu(String showThememenu) {
		this.showThememenu = showThememenu;
	}

	public String getShowFilter() {
		return showFilter;
	}

	public void setShowFilter(String showFilter) {
		this.showFilter = showFilter;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getNavigation() {
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}

	public String getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	public String getSecured() {
		return secured;
	}

	public void setSecured(String secured) {
		this.secured = secured;
	}

	public String getIntranet() {
		return intranet;
	}

	public void setIntranet(String intranet) {
		this.intranet = intranet;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}


	
}
