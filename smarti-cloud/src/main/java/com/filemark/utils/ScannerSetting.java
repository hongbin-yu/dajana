package com.filemark.utils;

import java.util.HashMap;
import java.util.Map;

public class ScannerSetting {
	
	
	private String commit = "YES";
	private String autocommit = "NO";
	private String scanner = "NO";
	private String setting = "YES";
	private String scanmore="NO";
	private String scanlocal="YES";
	private String fixedBarcode="NO";
	private int rotate=0;
	private int transferCount=200;
	private long barcodeOriention = 0;
	private long barcodeType=0;
	private Map<Object, Object> session = new HashMap<Object, Object>();
	
	public ScannerSetting() {
		super();
	}
	
	public void copy(ScannerSetting setting) {
		this.commit = setting.getCommit();
		this.autocommit = setting.getAutocommit();
		this.scanner = setting.getScanner();
		this.setting = setting.setting;
		this.scanmore = setting.getScanmore();
		this.scanlocal = setting.getScanlocal();
		this.fixedBarcode = setting.getFixedBarcode();
		this.rotate = setting.getRotate();
		this.transferCount = setting.getTransferCount();
		this.barcodeOriention = setting.getBarcodeOriention();
		this.barcodeType = setting.getBarcodeType();
	}
	public String getCommit() {
		return commit;
	}
	public void setCommit(String commit) {
		this.commit = commit;
	}
	public String getAutocommit() {
		return autocommit;
	}
	public void setAutocommit(String autocommit) {
		this.autocommit = autocommit;
	}
	public String getScanner() {
		return scanner;
	}
	public void setScanner(String scanner) {
		this.scanner = scanner;
	}
	public String getSetting() {
		return setting;
	}
	public void setSetting(String setting) {
		this.setting = setting;
	}
	public String getScanmore() {
		return scanmore;
	}
	public void setScanmore(String scanmore) {
		this.scanmore = scanmore;
	}
	public String getScanlocal() {
		return scanlocal;
	}
	public void setScanlocal(String scanlocal) {
		this.scanlocal = scanlocal;
	}
	public String getFixedBarcode() {
		return fixedBarcode;
	}
	public void setFixedBarcode(String fixedBarcode) {
		this.fixedBarcode = fixedBarcode;
	}
	public int getRotate() {
		return rotate;
	}
	public void setRotate(int rotate) {
		this.rotate = rotate;
	}
	public int getTransferCount() {
		return transferCount;
	}
	public void setTransferCount(int transferCount) {
		this.transferCount = transferCount;
	}
	
	public long getBarcodeOriention() {
		return barcodeOriention;
	}
	public void setBarcodeOriention(long barcodeOriention) {
		this.barcodeOriention = barcodeOriention;
	}
	public long getBarcodeType() {
		return barcodeType;
	}
	public void setBarcodeType(long barcodeType) {
		this.barcodeType = barcodeType;
	}
	public Map<Object, Object> getSession() {
		return session;
	}
	public void setSession(Map<Object, Object> session) {
		this.session = session;
	}

	
}
