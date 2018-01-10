package com.filemark.utils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ScanUploadForm {

	private String filename;
	private String contentType;
	private List<MultipartFile> file;
	private List<MultipartFile> upload;
	public ScanUploadForm() {
		super();
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public List<MultipartFile> getFile() {
		return file;
	}
	public void setFile(List<MultipartFile> files) {
		this.file = files;
	}
	public List<MultipartFile> getUpload() {
		return upload;
	}
	public void setUpload(List<MultipartFile> upload) {
		this.upload = upload;
	}
	
	
}
