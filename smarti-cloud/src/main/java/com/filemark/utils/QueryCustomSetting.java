package com.filemark.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;






import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class QueryCustomSetting implements java.io.Serializable {
	
	private String appsSecurity;
	private Map<String,Object> repository;
	private int menuSize = 10;
	private int pageSize = 10 ;
	private int maxClauseSet =1024;
	private int maxPageToOpen =1000;
	private int displayLevel = 4;
	private int orderBy = 10;
	private String sortBy = "ASC";
	private String drawSort = "draw.name ASC";
	private String foldSort = "fold.name ASC";
	private String docSort = "doc.name ASC";
	private static final Logger logger = LoggerFactory.getLogger(QueryCustomSetting.class);
	
	public QueryCustomSetting() {
		super();
		repository = new HashMap<String, Object>();
	}

	public QueryCustomSetting(QueryCustomSetting querySystemSetting) {
		this.displayLevel = querySystemSetting.getDisplayLevel();
		this.menuSize = querySystemSetting.getMenuSize();
		this.pageSize = querySystemSetting.getPageSize();
		this.orderBy = querySystemSetting.getOrderBy();
		this.sortBy = querySystemSetting.getSortBy();
		this.drawSort = querySystemSetting.getDrawSort();
		this.foldSort = querySystemSetting.getFoldSort();
		this.docSort = querySystemSetting.getDocSort();
		this.maxPageToOpen = querySystemSetting.getMaxPageToOpen();
		this.repository = new HashMap<String, Object>();
	}

	public void copy(QueryCustomSetting querySystemSetting) {
		this.displayLevel = querySystemSetting.getDisplayLevel();
		this.menuSize = querySystemSetting.getMenuSize();
		this.pageSize = querySystemSetting.getPageSize();
		this.orderBy = querySystemSetting.getOrderBy();
		this.sortBy = querySystemSetting.getSortBy();
		this.drawSort = querySystemSetting.getDrawSort();
		this.foldSort = querySystemSetting.getFoldSort();
		this.docSort = querySystemSetting.getDocSort();
		this.maxPageToOpen = querySystemSetting.getMaxPageToOpen();
	}




	private String getFilterValue(String field, String operator, String value) {
		String op = operator.toLowerCase();
		String va = value.toLowerCase();
		if (va.indexOf(" ")>0)
			va = "\""+va+"\"";
		String operatorValue = va;
		if ("begins".equals(op)) {
			operatorValue=va+"*";
		} else if (operator.equals(">") || operator.equals(">=")) {
			operatorValue="["+va+" TO ZZZZ]";
		} else if (operator.equals("<") || operator.equals("<=")) {
			operatorValue="[0000"+" TO "+va+"]";;
		}else if ("between".equals(op)) {
			operatorValue="["+va.replace("and", "TO")+"]";
		}

		return operatorValue;
	}



	
	
	public String getAppsSecurity() {
		return appsSecurity;
	}

	public void setAppsSecurity(String appsSesurity) {
		this.appsSecurity = appsSesurity;
	}

	public int getMenuSize() {
		return menuSize;
	}

	public void setMenuSize(int menuSize) {
		this.menuSize = menuSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplayLevel(int displayLevel) {
		this.displayLevel = displayLevel;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getDrawSort() {
		return drawSort;
	}

	public void setDrawSort(String drawSort) {
		this.drawSort = drawSort;
	}

	public String getFoldSort() {
		return foldSort;
	}

	public void setFoldSort(String foldSort) {
		this.foldSort = foldSort;
	}

	public String getDocSort() {
		return docSort;
	}

	public void setDocSort(String docSort) {
		this.docSort = docSort;
	}

	public int getMaxPageToOpen() {
		return maxPageToOpen;
	}

	public void setMaxPageToOpen(int maxPageToOpen) {
		this.maxPageToOpen = maxPageToOpen;
	}

	public Map<String, Object> getRepository() {
		return repository;
	}

	public void setRepository(Map<String, Object> repository) {
		this.repository = repository;
	}
	



	public int getMaxClauseSet() {
		return BooleanQuery.getMaxClauseCount();
	}

	public void setMaxClauseSet(int maxClauseSet) {
		BooleanQuery.setMaxClauseCount(maxClauseSet);
		this.maxClauseSet = maxClauseSet;
	}
	
	
}
