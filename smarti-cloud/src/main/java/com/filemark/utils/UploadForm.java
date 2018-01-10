package com.filemark.utils;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadForm {

	private Long appId;
	private Long id;
	private Long pageNo;
    private String name = null;
    private CommonsMultipartFile file = null;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public CommonsMultipartFile getFile() {
        return file;
    }
    public void setFile(CommonsMultipartFile file) {
        this.file = file;
        this.name = file.getOriginalFilename();
    }
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPageNo() {
		return pageNo;
	}
	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}
    
}
