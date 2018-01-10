/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.filemark.jcr.controller;



import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.util.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.sso.JwtUtil;


@Controller
public class SigninController extends BaseController{
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "dajanaSigningKey";

	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(String redirect, HttpServletRequest request, HttpServletResponse response) {
/*		if(redirect!=null && "signin".equals(redirect)) return "signin";
        String username = JwtUtil.getSubject(request, jwtTokenCookieName, signingKey);
        if(username != null){
        	String authors[] = username.split("/");
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			User user = new User();
			user.setUserName(authors[1]);
        	if(authors[0]==null || "null".equals(authors[0])) {
        		authors[0] = "";
        	}			
			user.setHost(authors[0]);
			user.setTitle(authors[2]);
			user.setSigningKey(authors[3]);			
        	try {
        		if(jcrService.nodeExsits("/system/users/"+authors[1].toLowerCase())) {
        			user = (User)jcrService.getObject("/system/users/"+authors[1].toLowerCase());

        			for(Role role:user.getRoles()) {
        				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
        			}
        			
        			
        		}
    			for(int i=3; i<authors.length; i++) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+authors[i].toUpperCase()));
    				
    			}
				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));//default role
				org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),true,true,true,true,authorities);
				    			
    			Authentication auth = 
    			new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
    			SecurityContext securityContext = SecurityContextHolder.getContext();
    			securityContext.setAuthentication(auth);    			
    			HttpSession session = request.getSession(true);
    	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    			request.setAttribute("usersite", user.getHost());
    			request.setAttribute("username", user.getName());
    			request.setAttribute("usertitle", user.getTitle());
    			request.setAttribute("signingKey", user.getSigningKey());    			
        		if(redirect !=null)
        			return "redirect:"+redirect;
        		else
        			return "redirect:"+authors[0]+"/editor.html?path=/content/"+authors[1];

        	} catch (RepositoryException e) {
				Log.error(e.getMessage());
			}
        }*/
				
    	return "signin";		
	}

	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public void register(String redirect, HttpServletRequest request, HttpServletResponse response) {
	}
	
	private String getRolePrefix() {
		
		return "ROLE_";
	}

}
