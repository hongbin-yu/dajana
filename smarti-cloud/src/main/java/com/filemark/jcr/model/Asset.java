package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jcr.Property;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

//@Node(jcrMixinTypes = "mix:versionable")
@Node(jcrMixinTypes = "mix:referenceable")
public class Asset implements SmartiNode, Serializable {

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
	protected String alt;
	@Field
	protected String url;
	@Field
	protected String description;		
	@Field
	protected String contentType;	
	@Field	
	private String createdBy;
	@Field	
	private String device;
	@Field 
	private Date lastModified;
	@Field 
	private Date originalDate;
	@Field
	private String html;
	@Field
	private Long size;
	@Field
	private String ext;	
	@Field
	private Long width;	
	@Field
	private Long height;
	@Field
	private String icon;
	@Field
	private String pdf;
	
	private String ocm_classname;
	private String nodeName;
	private String parent;
	
	public Asset() {
		super();

	}	
	
	public Asset(String path, String title) {
		super();
		this.path = path;
		this.title = title;
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



	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public Date getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(Date originalDate) {
		this.originalDate = originalDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getIcon() {
		if(icon !=null) {
			return icon;
		}
		if(contentType == null) {
			return "resources/images/document-icon.png";
		}else if((contentType.equals("image/tiff") || contentType.equals("image/x-tiff") )) {
			return "resources/images/pdf-icon.png";
		}else if(contentType.startsWith("image/")) {
			return "viewimage?path="+path+"&w=4";
		}else if(contentType.startsWith("video/")) {
			return "video2jpg.jpg?path="+path;			
		}else if(contentType.startsWith("audio/")) {
			return "resources/images/audio-icon.png";	
		}else if("application/pdf".equals(contentType)){
			return "pdf2jpg.jpg?path="+path;//"resources/images/pdf-icon.png";
		}else if("application/vnd.ms-word".equals(contentType) || "application/msword".equals(contentType) || path.endsWith(".doc") || path.endsWith(".docx")) {	
			return "doc2jpg.jpg?path="+path;
		}else if("application/vnd.ms-excel".equals(contentType) || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
			return "resources/images/excel-icon.png";
		}else if(path.endsWith(".zip")) {
			return "resources/images/zip-icon.png";
		}else {	
			return "resources/images/document-icon.png";
		}
	}

	public String getPdf() {
		if(contentType != null && contentType.startsWith("image/")) {
			return "pdf";
		}else {	
			return null;
		}
	}

	public boolean getDoc2pdf() {
		if(contentType != null && ("application/vnd.ms-word".equals(contentType) || "application/msword".equals(contentType) || path.endsWith(".doc") || path.endsWith(".docx"))) {
			return true;
		}else {	
			return false;
		}
	}	
	public boolean getText() {
		if(contentType != null && (contentType.startsWith("text/") || contentType.startsWith("application/json"))) {
			return true;
		}else {	
			return false;
		}
	}
	public String getLink() {
		if(contentType != null && (contentType.equals("image/tiff") || contentType.equals("image/x-tiff") )) {
			return "viewpdf?path="+path;
		}else if(contentType.startsWith("video/")) {
			return "video.mp4?path="+path+".mp4";
		}else{
			return "viewimage?path="+path;
		}
	}

	public String getCssClass() {
		if(contentType != null && contentType.startsWith("image/")) {
			return "wb-lbx";
		}else {	
			return "download";
		}
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}


	
}
