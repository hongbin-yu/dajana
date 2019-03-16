package com.filemark.jcr.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

//@Node(jcrMixinTypes = "mix:versionable")
@Node(jcrMixinTypes = "mix:referenceable")
public class Asset implements SmartiNode, Serializable {

	/**
	 * 
	 */
	private static String devicePath="/mnt/device";
	private static final long serialVersionUID = 1L;
	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field(jcrName="jcr:title")
	protected String title;
	@Field
	protected String filePath;	
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
	private Date modifiedDate;	
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
	private Long total = 1l;	
	@Field
	private String icon;
	@Field
	private String position;
	@Field
	private String status="lock";
	
	private String ocm_classname = Asset.class.getSimpleName().toLowerCase();
	private String nodeName;
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
		if(name==null && path !=null) {
		   return path.substring(path.lastIndexOf("/")+1);	
		}
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
		return path.substring(0, path.lastIndexOf("/"));
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
	public String getCreatedDate() {
		if(originalDate == null) originalDate = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(originalDate);
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

	public String getIcon() {
		//if(icon !=null) {
			//return icon;
		//}
		if(contentType == null) {
			return "/resources/images/document-icon.png";
		}else if((contentType.equals("image/tiff") || contentType.equals("image/x-tiff") )) {
			return "/resources/images/pdf-icon.png";
		}else if(contentType.startsWith("image/")) {
			return "viewimage?path="+path+"&w=4";
		}else if(contentType.startsWith("video/")) {
			return "video2jpg.jpg?path="+path;			
		}else if(contentType.startsWith("audio/")) {
			return "/resources/images/audio-icon.png";	
		}else if("application/pdf".equals(contentType)){
			return "pdf2img.jpg?p=0&path="+path+"&w=4";//"resources/images/pdf-icon.png";
		}else if(path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".ppt")) {	
			return "doc2jpg.jpg?p=0&path="+path;
		}else if(path.toLowerCase().endsWith(".xls") || path.toLowerCase().endsWith(".xlsx") ||  path.toLowerCase().endsWith(".csv")  || path.toLowerCase().endsWith(".rtf")) {
			//if(getDoc2pdf()) {
			return "doc2jpg.jpg?path="+path;
			//}
			//return "resources/images/excel-icon.png";
		}else if(path.endsWith(".zip")) {
			return "/resources/images/zip-icon.png";
		}else {	
			return "/resources/images/document-icon.png";
		}
	}
	public String getIconSmall() {
		//if(icon !=null) {
			//return icon;
		//}
		if(contentType == null) {
			return "/resources/images/document-icon100.png";
		}else if((contentType.equals("image/tiff") || contentType.equals("image/x-tiff") )) {
			return "/resources/images/pdf-icon100.png";
		}else if(contentType.startsWith("image/")) {
			return "viewimage?path="+path+"&w=1";
		}else if(contentType.startsWith("video/")) {
			return "video2jpg.jpg?path="+path+"&w=1";			
		}else if(contentType.startsWith("audio/")) {
			return "/resources/images/audio-icon100.png";	
		}else if("application/pdf".equals(contentType)){
			return "pdf2img.jpg?p=0&path="+path+"&w=1";//"resources/images/pdf-icon.png";
		}else if(path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".ppt")) {	
			return "doc2jpg.jpg?p=0&path="+path+"&w=1";
		}else if(path.toLowerCase().endsWith(".xls") || path.toLowerCase().endsWith(".xlsx") ||  path.toLowerCase().endsWith(".csv")  || path.toLowerCase().endsWith(".rtf")) {
			//if(getDoc2pdf()) {
			return "doc2jpg.jpg?path="+path+"&w=1";
			//}
			//return "resources/images/excel-icon.png";
		}else if(path.endsWith(".zip")) {
			return "/resources/images/zip-icon100.png";
		}else {	
			return "/resources/images/document-icon100.png";
		}
	}
	public boolean getPdf() {
		if(contentType != null && contentType.startsWith("image/")) {
			return true;
		}else {	
			return false;
		}
	}

	public boolean getDoc2pdf() {
		if(path==null) return false;
		if(path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".ppt") || path.toLowerCase().endsWith(".xls") || path.toLowerCase().endsWith(".xlsx") ||  path.toLowerCase().endsWith(".csv")  || path.toLowerCase().endsWith(".rtf")) {
				return true;
		}
		return false;
	}
	
	public boolean getMp4() {
		
		if(contentType != null && contentType.startsWith("video/")) {

			return true;
		}
		
		return false;
	}
	
	public boolean getAudio() {
		
		if(contentType != null && contentType.startsWith("audio/")) {

			return true;
		}
		
		return false;
	}	
	
	public boolean getText() {
		if(contentType != null && (contentType.startsWith("text/") || contentType.startsWith("application/json") || contentType.startsWith("application/javascript"))) {
			return true;
		}else {	
			return false;
		}
	}
	public String getLink() {
		if(contentType == null)
			return "/resources/images/usericon.png";
		if(contentType.equals("image/tiff") || contentType.equals("image/x-tiff") ) {
			return "/viewpdf?path="+path;
		}else if(contentType.startsWith("video/")) {
			return "/video.mp4?path="+path;
		}else{
			return "youcloud/"+getUid()+"/"+getTitle()+(getTitle() == null || getExt()==null || getTitle().endsWith(getExt())?"":getExt());//+"?path="+path;
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

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public static String getDevicePath() {
		return devicePath;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public static void setDevicePath(String devicePath) {
		Asset.devicePath = devicePath;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		if(status==null) return "lock";
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	
}
