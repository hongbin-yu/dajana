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

import com.filemark.jcr.model.User;
import com.filemark.sso.JwtUtil;

public class AdminFilter implements Filter {
	private FilterConfig filterConfig;
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "dajanaSigningKey";	
	private static final Logger logger = LoggerFactory.getLogger(AdminFilter.class);
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        String authService = this.getFilterConfig().getInitParameter("services.auth");
        String authKey = this.getFilterConfig().getInitParameter("services.key");
        String redirectUrl = httpServletRequest.getRequestURL().toString() ;
        String signingUser = JwtUtil.getSubject(httpServletRequest, jwtTokenCookieName, signingKey);
        if(!httpServletRequest.getContextPath().equals("/") && !authService.startsWith("http")) {
        	authService = httpServletRequest.getContextPath()+authService;
        }
        if(signingUser==null) {
        	if(getUsername()!=null) {
        	    chain.doFilter(httpServletRequest, httpServletResponse);
        	}else {
	        	logger.debug("no signingUser "+authService + "?redirect=" + redirectUrl);
        		httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl);
        	}
        } else {
        	String usersite = (String)httpServletRequest.getAttribute("usersite");
        	String port = (String)httpServletRequest.getAttribute("port");
        	String username = (String)httpServletRequest.getAttribute("username");
        	String domain = httpServletRequest.getServerName();
        	if(!"".equals(usersite) && !domain.equals(usersite)) {
            	httpServletResponse.sendRedirect("http://"+usersite+port+"/protected/mysite"+"?domain="+domain);
        	}
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authen = securityContext.getAuthentication();
            String authors[] = signingUser.split("/");
            if(!authors[4].equals(authKey)) {
            	
                httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl+"&keyerror=1");
            }
            if(authen == null || !authen.getName().equals(authors[2])) {
    			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    			User user = new User();
    			user.setHost(authors[0]);
    			user.setPort(authors[1]);
    			user.setUserName(authors[2]);
    			user.setTitle(authors[3]);
    			user.setSigningKey(authors[4]);	
    			httpServletRequest.setAttribute("usersite", authors[0]);
    			httpServletRequest.setAttribute("port", authors[1]);
    			httpServletRequest.setAttribute("username", authors[2]);
    			httpServletRequest.setAttribute("usertitle", authors[3]);
    			httpServletRequest.setAttribute("signingKey", authors[4]);	
    			if(authors.length < 5) {
    	            httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl);
    			}
    			for(int i=5; i<authors.length; i++) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+authors[i].toUpperCase()));
    				
    			}
    			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));//default role
    			
    			org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"protected",true,true,true,true,authorities);
    			
    			Authentication auth = 
    			new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
    			securityContext.setAuthentication(auth);    			
    			HttpSession session = httpServletRequest.getSession(true);
    	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);  
    			session.setAttribute("usersite", authors[0]);
    			session.setAttribute("port", authors[1]);    			
    			session.setAttribute("username", authors[2]);
    			session.setAttribute("usertitle", authors[3]);	 
    			session.setAttribute("signingKey", authors[4]);	                  	
            }
  	
    	    chain.doFilter(httpServletRequest, httpServletResponse);
        	
        }


	}
	
	protected String getUsername() {
		if(SecurityContextHolder.getContext()==null || SecurityContextHolder.getContext().getAuthentication()==null) return null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth!=null && auth.isAuthenticated()) { 
			return auth.getName();
		} else
			return null;
	}		
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	private String getRolePrefix() {
		
		return "ROLE_";
	}   
}
