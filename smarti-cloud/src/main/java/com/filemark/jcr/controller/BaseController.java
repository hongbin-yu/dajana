package com.filemark.jcr.controller;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.QueryCustomSetting;


public class BaseController {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	@Inject
	protected JcrServices jcrService;
	@Inject	
	protected QueryCustomSetting queryCustomSetting;	
	@Autowired
	private ServletContext servletContext;
	@Autowired
	protected SessionLocaleResolver localeResolver;
	@Autowired
	protected ResourceBundleMessageSource messageSource;
	@PostConstruct
	private void setUp() {
		/*
			if(!jcrService.nodeExsits(Folder.root)) {
				try {
					jcrService.addNodes(Folder.root,"nt:unstructured","fmdba");
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
				}
			}
			*/
		Asset.setDevicePath(jcrService.getDevice());
		ImageUtil.HDDOff();
	}

	

	
    

	protected String getNavigation() throws RepositoryException {
		String username = getUsername();
		if(queryCustomSetting.getRepository().containsKey("navigation_"+username)) {
			return (String)queryCustomSetting.getRepository().get("navigation_"+username);
		}else {
			String navigation = jcrService.getNavigation(Folder.root,getPermission());
			queryCustomSetting.getRepository().put("navigation_"+username,navigation);
			return navigation;
		}

	}
	
	protected Object getRoles() {
		if(queryCustomSetting.getRepository().containsKey("roles")) {
			return queryCustomSetting.getRepository().get("roles");
		}else {
			Object roles = jcrService.getObjects(Folder.root+"/system/roles//",Role.class);
			queryCustomSetting.getRepository().put("roles",roles);
			return roles;
		}
	}
	
	protected String getPermission() throws RepositoryException {
		String username = getUsername();
		if(username==null) return "";
		if(queryCustomSetting.getRepository().containsKey("permission_"+username)) {
			return (String)queryCustomSetting.getRepository().get("permission_"+username);
		}else{
			getGroupsPermission();				
			return (String)queryCustomSetting.getRepository().get("permission_"+username);
		}

	}
		
	protected String getGroupsPermission() throws RepositoryException {
		String groups = "guest";	
		String username = getUsername();
		if(username==null) return "";
		if(queryCustomSetting.getRepository().containsKey("groups_"+username)) {
			return (String)queryCustomSetting.getRepository().get("groups_"+username);
		}else {
			User smartiUser = (com.filemark.jcr.model.User)jcrService.getObject(Folder.root+"/system/users/"+username.toLowerCase());
			if(smartiUser == null) {
		    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	    	if (principal instanceof UserDetails) {
    	    		Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
    	    		if(authorities.size() > 0) {


    	    			for (GrantedAuthority authoritie:authorities) {
    	    				if(groups.equals("")) {
    	    					groups =authoritie.getAuthority().toLowerCase().replaceAll("role_", "");
    	    				}else {
    	    					groups +=" OR "+authoritie.getAuthority().toLowerCase().replaceAll("role_", "");
    	    				}

    	    			}

    	    		}
    	    	}					
			}else
			for(Role role:smartiUser.getRoles()) {
				if(groups.equals("")) {
					groups =role.getRoleName().toLowerCase().replaceAll("role_", "");
				}else {
					groups +=" OR "+role.getRoleName().toLowerCase().replaceAll("role_", "");
				}				
			}
			queryCustomSetting.getRepository().put("groups_"+username,groups);
			String permission = groups;
			if(!"".equals(groups)) {
				permission =" AND (s.groups is null OR s.groups LIKE '' OR CONTAINS(s.groups,'"+groups+"'))";
			}
			queryCustomSetting.getRepository().put("permission_"+username,permission);

		}


		return groups;
	}
	
	protected String[] getPaths(HttpServletRequest request) throws UnsupportedEncodingException {
		 //String contentPath = request.getContextPath().toLowerCase();
		 String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		 String paths[] = path.split("\\.");
		 
		 return paths;
	}

	protected String getUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		 //String contentPath = request.getContextPath().toLowerCase();
		 String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			String url = request.getQueryString();
			if(url == null)
				url = path;
			else
				url = path+"?"+url;
		 
		 return url;
	}

	
	protected String getUsername() {
		if(SecurityContextHolder.getContext()==null || SecurityContextHolder.getContext().getAuthentication()==null) return null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth!=null && auth.isAuthenticated()) { 
			return auth.getName();
		} else
			return null;
	}
	
	protected org.springframework.security.core.userdetails.User getUser() {
		if(SecurityContextHolder.getContext()==null || SecurityContextHolder.getContext().getAuthentication()==null) return null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth!=null && auth.isAuthenticated()) { 
			return (org.springframework.security.core.userdetails.User)auth;
		} else
			return null;
		
	}
	
	protected boolean isRole(String role) {
		for(GrantedAuthority authority:SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			if(authority.getAuthority().equalsIgnoreCase(role)) return true;
		}
		return false;
	}
	protected @ResponseBody String deleteNode(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		String result="";
		//String contentPath = request.getContextPath();
		String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		//path = path.replaceFirst(contentPath, "");
		String paths[] = path.split("\\.delete");
		jcrService.getBreadcrumb(paths[0],getPermission());		
		try {
			result = jcrService.deleteNode(paths[0]);

		}catch(Exception e) {	
			result = e.getMessage();
		}

		return result;
	} 
	@SuppressWarnings("unused")
	private String getPublicIpAddress() {
	    String res = null;
	    try {
	        String localhost = InetAddress.getLocalHost().getHostAddress();
	        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
	        while (e.hasMoreElements()) {
	            NetworkInterface ni = e.nextElement();
	            if(ni.isLoopback())
	                continue;
	            if(ni.isPointToPoint())
	                continue;
	            Enumeration<InetAddress> addresses = ni.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress address = addresses.nextElement();
	                if(address instanceof Inet4Address) {
	                    String ip = address.getHostAddress();
	                    if(!ip.equals(localhost))
	                        System.out.println((res = ip));
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res;
	}

	private static final String[] IP_HEADER_CANDIDATES = { 
	    "X-Forwarded-For",
	    "Proxy-Client-IP",
	    "WL-Proxy-Client-IP",
	    "HTTP_X_FORWARDED_FOR",
	    "HTTP_X_FORWARDED",
	    "HTTP_X_CLUSTER_CLIENT_IP",
	    "HTTP_CLIENT_IP",
	    "HTTP_FORWARDED_FOR",
	    "HTTP_FORWARDED",
	    "HTTP_VIA",
	    "REMOTE_ADDR" };
	
	public static String getClientIpAddress(HttpServletRequest request) {
	    for (String header : IP_HEADER_CANDIDATES) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}

	public static boolean isIntranet(HttpServletRequest request) {
		String ip = getClientIpAddress(request);
		return (ip.startsWith("192.") || ip.startsWith("172.")  || ip.startsWith("10.") || ip.startsWith("0:0:") || ip.startsWith("127.") );
	}
	
	protected void invalidCache() {
		queryCustomSetting.getRepository().clear();
	}


	public SessionLocaleResolver getLocaleResolver() {
		return localeResolver;
	}


	public void setLocaleResolver(SessionLocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	
}
