package com.filemark.sso.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.filemark.jcr.controller.SiteController;
import com.filemark.jcr.model.User;
import com.filemark.sso.JwtUtil;


@Component
public class JwtFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private FilterConfig filterConfig;


	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
    	String domain = httpServletRequest.getServerName();
    	if(getUsername()==null) {
            String signingUser = JwtUtil.getSubject(httpServletRequest, JwtUtil.jwtTokenCookieName, JwtUtil.signingKey,domain);
            if(signingUser != null){
                String authors[] = signingUser.split("/");
    			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    			User user = new User();
    			user.setHost(authors[0]);
    			user.setPort(authors[1]);
    			user.setUserName(authors[2]);
    			user.setTitle(authors[3]);
    			user.setSigningKey(authors[4]);	
    			httpServletRequest.setAttribute("signingUser", signingUser);
    			httpServletRequest.setAttribute("usersite", authors[0]);
    			httpServletRequest.setAttribute("port", authors[1]);			
    			httpServletRequest.setAttribute("username", authors[2]);
    			httpServletRequest.setAttribute("usertitle", authors[3]);
    			httpServletRequest.setAttribute("signingKey", authors[4]);			
    			for(int i=4; i<authors.length; i++) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+authors[i].toUpperCase()));
    				
    			}
    			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));//default role
    			
    			org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"protected",true,true,true,true,authorities);
    			
    			Authentication auth = 
    			new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
    			SecurityContext securityContext = SecurityContextHolder.getContext();
    			securityContext.setAuthentication(auth);    			
    			HttpSession session = httpServletRequest.getSession(true);
    	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);  
    			session.setAttribute("usersite", authors[0]);
    			session.setAttribute("port", authors[1]);	
    			session.setAttribute("username", authors[2]);
    			session.setAttribute("usertitle", authors[3]);	 
    			session.setAttribute("signingKey", authors[4]);					
            	
            }    		
    	}

        chain.doFilter(httpServletRequest, httpServletResponse);

        /*        if(signingUser == null){
            String authService = this.getFilterConfig().getInitParameter("services.auth");
//            String unprotected = this.getFilterConfig().getInitParameter("services.unprotected");
            String redirectUrl = httpServletRequest.getRequestURL().toString() ;
            String contentPath = httpServletRequest.getContextPath();
            String content="/content",login="/login", logout="/logout",resources="/resources";
            String uri = httpServletRequest.getRequestURI();
            if(!authService.startsWith("http")) {
            	authService = contentPath+authService;
            	login = contentPath+login;
            	logout = contentPath+logout;
            	content = contentPath+content;
            	resources = contentPath+resources;
            	unprotected = contentPath+unprotected;
            }

            //if((!uri.startsWith(resources) && !uri.startsWith(logout) && !uri.startsWith(login) &&  !uri.startsWith(unprotected)) || uri.endsWith(".edit"))
            if(redirectUrl!=null)	
            	httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl);
            else
            	httpServletResponse.sendRedirect(authService);
            //else
        		chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
                String authors[] = signingUser.split("/");
				Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				User user = new User();
				user.setUserName(authors[1]);
				user.setHost(authors[0]);
				user.setTitle(authors[2]);
				user.setSigningKey(authors[3]);	
				httpServletRequest.setAttribute("signingUser", signingUser);
				httpServletRequest.setAttribute("usersite", authors[0]);
				httpServletRequest.setAttribute("username", authors[1]);
				httpServletRequest.setAttribute("usertitle", authors[2]);
				httpServletRequest.setAttribute("signingKey", authors[3]);			
				for(int i=4; i<authors.length; i++) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+authors[i].toUpperCase()));
    				
    			}
				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));//default role
    			
				org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"protected",true,true,true,true,authorities);
    			
    			Authentication auth = 
    			new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
    			SecurityContext securityContext = SecurityContextHolder.getContext();
    			securityContext.setAuthentication(auth);    			
    			HttpSession session = httpServletRequest.getSession(true);
    	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);  
				session.setAttribute("usersite", authors[0]);
				session.setAttribute("username", authors[1]);
				session.setAttribute("usertitle", authors[2]);	 
				session.setAttribute("signingKey", authors[3]);					
    	        chain.doFilter(httpServletRequest, httpServletResponse);
	
        }*/
		
	}
	protected String getUsername() {
		if(SecurityContextHolder.getContext()==null || SecurityContextHolder.getContext().getAuthentication()==null) return null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth!=null && auth.isAuthenticated()) { 
			return auth.getName();
		} else
			return null;
	}	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	private String getRolePrefix() {
		
		return "ROLE_";
	}      
    
}
