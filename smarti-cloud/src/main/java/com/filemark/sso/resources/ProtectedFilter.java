package com.filemark.sso.resources;

import java.io.IOException;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.filemark.jcr.controller.ContentController;
import com.filemark.jcr.model.User;
import com.filemark.sso.CookieUtil;
import com.filemark.sso.JwtUtil;

public class ProtectedFilter implements Filter {
	private FilterConfig filterConfig;
	private static final Logger logger = LoggerFactory.getLogger(ProtectedFilter.class);
	     
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
        String redirectUrl = httpServletRequest.getRequestURL().toString() ;
    	String domain = httpServletRequest.getServerName();
        String signingUser = JwtUtil.getSubject(httpServletRequest, JwtUtil.jwtTokenCookieName, JwtUtil.signingKey,domain);
        if(!httpServletRequest.getContextPath().equals("/") && !authService.startsWith("http")) {
        	authService = httpServletRequest.getContextPath()+authService;
        }
        if(signingUser==null) {
    		//SecurityContext securityContext = SecurityContextHolder.getContext();
/*    		HttpSession session = httpServletRequest.getSession(true);
    		SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
    		if(securityContext!=null) {
        		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    			if(auth!=null && auth.getName()!=null) {
            	    chain.doFilter(httpServletRequest, httpServletResponse);   				
    			}else {
    	        	logger.debug("no signingUser and authen user"+authService + "?redirect=" + redirectUrl);
    	            httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl+"&authenUser=0");
   				
    			}
    		}else {

	        	logger.debug("no signingUser "+authService + "?redirect=" + redirectUrl);
	            httpServletResponse.sendRedirect(authService + "?redirect=" + redirectUrl+"&signingUser=0");

    		}*/
        	logger.debug("no signingUser "+authService + "?redirect=" + redirectUrl);
  		
          } else {
            String authors[] = signingUser.split("/");

          	if(domain.equals(authors[0])) {

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
	    	    //chain.doFilter(httpServletRequest, httpServletResponse);
				logger.info("Create Cookie:"+domain);
		        CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, JwtUtil.generateToken(JwtUtil.signingKey, signingUser), false, 86400*30, domain);
          	}
        }
        chain.doFilter(httpServletRequest, httpServletResponse);

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

	
	
}
