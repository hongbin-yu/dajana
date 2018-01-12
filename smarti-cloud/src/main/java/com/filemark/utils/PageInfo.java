package com.filemark.utils;




public class PageInfo implements java.io.Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//  Page Types
    public static final int typeAFP = 3;
	public static final int typeCOLD = 0;
	public static final int typeIMAGE = 1;
	public static final int typeMETACODE = 4;
	public static final int typeNONE = -99;
	public static final int typeOLE_DOCUMENT = 2;
	public static final int typePCL = 5;
	public static final int typeRTF = 6;
	public static final int typeWORD = 7;
	public static final int typeXML = 8;
	public static final int typeZIP = 9;
	public static final int typeURL = 10;
	public static final int typePDF = typeOLE_DOCUMENT;
 
 //  Page Properties
	private int cols = 0;
	private int height = 0;
	private String name = "";
	private String page = "";
	private int rows = 0;
	private int type = typeNONE;
	private int width = 0;
	private int total = 1;

	public PageInfo() {
		super();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}
	
	

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public String getFileType() {
		int ext_pos=0;
		String rv="";
		if(name != null) {
			ext_pos = name.lastIndexOf(".");
			if (ext_pos>0) 
				rv = name.substring(ext_pos - 1, name.length()).toLowerCase();
		}
		return rv;
	}
	
}
