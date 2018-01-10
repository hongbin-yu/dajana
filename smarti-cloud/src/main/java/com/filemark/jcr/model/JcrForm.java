package com.filemark.jcr.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class JcrForm {

	private String q="";
	private String v="";
	private String path="";
	private String name = "saveQurey";
	private long s=0;
	private long p=0;
	private int n=-1;
	private int l=0;
	private int mp=100;
	private int d=5;
	private int c=1;//cache pdf
	
	private String[] uuid;
	
	
	
	public JcrForm() {
		super();

	}
	
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	public long getS() {
		return s;
	}
	public void setS(long s) {
		this.s = s;
	}
	public long getP() {
		return p;
	}
	public void setP(long p) {
		this.p = p;
	}

	public String[] getUuid() {
		return uuid;
	}

	public void setUuid(String[] uuid) {
		this.uuid = uuid;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}


	public void setV(String v) {
		this.v = v;
	}
	
	public String getV() {
		return this.v;
	}

	public String getJSearch() {
	
		if(q!=null && !q.equals("")) {
			return " and CONTAINS(s.*,'"+q+"')";
		} else 
			return "";
	}
	
	public String getJQuery() {
		String[] indexes = {"s.draw","s.fold","s.document","s.index1","s.index2","s.index3","s.index4","s.index5","s.index6"};
		
		if(v!=null && !v.equals("")) {
			return " and LOWER("+indexes[this.n] +") LIKE '"+v.toLowerCase()+"'";
		} else 
			return "";
	}

	public String getMQuery() {
		
		if(v!=null && !v.equals("") && n == l && n < 3) {
			return " and LOWER(s.name) LIKE '"+v+"'";
		} else 
			return "";
	}
	
	public String getSQL2PdfString() {
		String queryString="select * from [nt:base] AS s WHERE (ISDESCENDANTNODE(["+path+"]) or ISSAMENODE(["+path+"]))"+this.getJSearch()+this.getJQuery()+" and s.ocm_classname='com.filemark.model.Pages' order by s.draw,s.fold,s.document,s.rank";
		return queryString;
	}
	
	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}
	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public String getQueryString() {
		String qString = "";
		try {
			qString = "q="+URLEncoder.encode(q,"UTF-8")+"&v="+URLEncoder.encode(v,"UTF-8")+"&n="+n+"&l="+l+"&p="+p;
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		 return qString;
	}
	
	public String getSearchString() {
		String qString = "";
		try {
			qString = "q="+URLEncoder.encode(q,"UTF-8")+"&v="+URLEncoder.encode(v,"UTF-8")+"&n="+n+"&l="+l+"&s="+s;
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		 return qString;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
	

}
