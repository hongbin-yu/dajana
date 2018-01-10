package com.filemark.jcr.serviceImpl;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.JcrServices;
import com.filemark.jcr.model.Role;

@SuppressWarnings("deprecation")
public class JcrUserDetailImpl implements UserDetailsService {

	@Autowired
	private JcrServices jcrService;
	
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		com.filemark.jcr.model.User smartiUser = null;
		try {
			smartiUser = (com.filemark.jcr.model.User)jcrService.getObject("/system/users/"+username.toLowerCase());
		} catch (RepositoryException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for(Role role:smartiUser.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
		}

		User user = new User(smartiUser.getUserName(),smartiUser.getPassword(),true,true,true,true,authorities);

		return user;
	}

	private String getRolePrefix() {
		
		return "ROLE_";
	}

	public JcrServices getJcrService() {
		return jcrService;
	}

	public void setJcrService(JcrServices jcrService) {
		this.jcrService = jcrService;
	}

	
}
