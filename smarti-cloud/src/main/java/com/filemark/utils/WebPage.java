package com.filemark.utils;

import java.util.List;


public class WebPage<E> {

	private int pageNumber;
	private int pageSize;
	private long availablePages;
	private long pageCount;
	private List<E> items;
	public WebPage(int pageNumber, int pageSize, long total, List<E> items) {
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.pageCount = total;
		availablePages = (total+pageSize-1)/pageSize;
		this.items = items;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getAvailablePages() {
		return availablePages;
	}
	public void setAvailablePages(long availablePages) {
		this.availablePages = availablePages;
	}
	
	public long getPageCount() {
		return pageCount;
	}
	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}
	public List<E> getItems() {
		return items;
	}
	public void setItems(List<E> items) {
		this.items = items;
	}
	
	public int getStartPage() {
		if(this.pageNumber > this.pageSize/2 && this.availablePages > this.pageSize) {
			return this.pageNumber - this.pageSize/2;
		}else
			return 1;
	}


	
	
	
}
