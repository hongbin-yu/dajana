package com.filemark.jcr.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import com.filemark.jcr.model.SmartiNode;

@Node
public class Role implements SmartiNode,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	@Field
	private String roleName;
	@Field
	private long rank;
	@Field(path=true)
	private String path;
	private Set<User> users = new HashSet<User>();
	
	
	public Role() {
		
	}
	public Role(int id, String roleName, Set<User> users) {
		super();
		this.id = id;
		this.roleName = roleName;
		this.users = users;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((roleName == null) ? 0 : roleName.hashCode());
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
		Role other = (Role) obj;
		if (roleName == null) {
			if (other.roleName != null)
				return false;
		} else if (!roleName.equals(other.roleName))
			return false;
		return true;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}



	public long getRank() {
		return rank;
	}



	public void setRank(long rank) {
		this.rank = rank;
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getUid() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getType() {
		return Role.class.getSimpleName().toLowerCase();
	}
	
}
