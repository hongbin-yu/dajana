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



import java.net.MalformedURLException;
import java.net.URL;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.sso.CookieUtil;
import com.filemark.sso.JwtUtil;


@Controller
public class SigninController extends BaseController{

	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(String redirect, String username, HttpServletRequest request, HttpServletResponse response,Model model) {

        //String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
        //CookieUtil.clear(response, jwtTokenCookieName,domain);
        //request.getSession().invalidate();
		if(username==null) username="";
		model.addAttribute("j_username",username);
		model.addAttribute("loginCount", "0");
    	return "signin";		
	}

	@RequestMapping(value="/signin", method=RequestMethod.POST)
	public String signinPost(HttpServletRequest request,HttpServletResponse httpServletResponse, String j_username, String j_password, String redirect,Integer loginCount, Model model) throws RepositoryException {

    	User user = new User();
    	Page page = new Page();
    	String title = messageSource.getMessage("djn.login", null,"&#30331;&#20837;", localeResolver.resolveLocale(request));

    	page.setTitle(title);
    	String lastIp = getClientIpAddress(request);
    	if(loginCount==null) return signin(redirect,j_username, request, httpServletResponse,model);
    	loginCount++;
    	model.addAttribute("page", page);   
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	    	
        if (j_username == null || !jcrService.nodeExsits("/system/users/"+j_username.toLowerCase()) ){
        	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
        	
            model.addAttribute("error", login_error);
            return "signin";
        }else {
        	
        	user = (User)jcrService.getObject("/system/users/"+j_username.toLowerCase());
        	//if(user.getSigningKey() ==null || user.getSigningKey().equals("")) user.setSigningKey("dajana.cn");
        	
        	if(!user.getSigningKey().equals(j_password)) {
            	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
        		
                model.addAttribute("error", login_error);
                return "signin";        		
        	}
        	jcrService.updatePropertyByPath(user.getPath(), "lastIp", lastIp);
        }
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));
		for(Role role:user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
		} 
		if(user.getPort()==null || "".equals(user.getPort())) {
			user.setPort("");
		}else {
			user.setPort(":"+user.getPort());
		}
        String token_author = user.getHost()+"/"+user.getPort()+"/"+j_username+"/"+user.getTitle()+"/"+request.getRemoteAddr();
		for(Role role:user.getRoles()) {
			token_author += "/"+role.getRoleName();
		}
		org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"proptected",true,true,true,true,authorities);
		
		Authentication auth = 
		new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(auth);
		HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		request.setAttribute("usersite", user.getHost());
		request.setAttribute("port", user.getPort());
		request.setAttribute("username", j_username);
		request.setAttribute("usertitle", user.getTitle()); 
		request.setAttribute("signingKey", user.getSigningKey());  
        String token = JwtUtil.generateToken(JwtUtil.signingKey, token_author);
        String domain = request.getRemoteAddr();
        //(user.getHost()==null || "".equals(user.getHost()))? request.getServerName():user.getHost();
        if(redirect !=null) {
        	try {
				URL url = new URL(redirect);
				domain = url.getHost();
			} catch (MalformedURLException e) {
				redirect +="&error="+e.getMessage();
			}	
        }
        
        CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, token, false, -1, domain);
        if(user.getHost()!=null && !domain.equals(user.getHost()))
        	CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, token, false, -1, user.getHost());
        if(redirect==null || "".equals(redirect) || "signin".equals(redirect)) {
    		return "redirect:/site/assets.html";
        }else {
       		return "redirect:"+ redirect;
        }
		
	
	}	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public void register(String redirect, HttpServletRequest request, HttpServletResponse response) {
	}
	
	private String getRolePrefix() {
		
		return "ROLE_";
	}

}
