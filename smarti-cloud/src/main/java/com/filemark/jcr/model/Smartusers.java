package com.filemark.jcr.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.codehaus.jackson.annotate.JsonIgnore;

// Generated 11-Sep-2009 9:59:44 PM by Hibernate Tools 3.2.4.GA

/**
 * Smartusers generated by hbm2java
 */
@Entity
public class Smartusers implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String userName;
	@JsonIgnore
	private BigDecimal userId;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private Date createDate;
	@JsonIgnore
	private Set<Smartgroups> smartgroups = new HashSet<Smartgroups>(0);
	
	public Smartusers() {
	}

	
/*	public Smartusers(String userName) {
		super();
		this.userName = userName;
	}
*/

	public Smartusers(int id, String userName, String password) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
	}


	public Smartusers(String userName,BigDecimal userId) {
		this.userName = userName;
		this.userId = userId;
	}

	public Smartusers(String userName, BigDecimal userId, String password) {
		this.userName = userName;
		this.userId = userId;
		this.password = password;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getUserId() {
		return this.userId;
	}

	public void setUserId(BigDecimal userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Smartgroups> getSmartgroups() {
		return smartgroups;
	}


	public void setSmartgroups(Set<Smartgroups> smartgroups) {
		this.smartgroups = smartgroups;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Smartusers other = (Smartusers) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
