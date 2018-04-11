package com.filemark.jcr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
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
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.QueryCustomSetting;
import com.filemark.utils.WebPage;



public class BaseController {
	
    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
	private static int loadDTKBarReader = 0;
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
		if(loadDTKBarReader==0) {
			loadDTKBarReader = 1;
			Asset.setDevicePath(jcrService.getDevice());
			if(!jcrService.nodeExsits("/system/devices/backup")) {
				Device backup = new Device(jcrService.getBackup());
				backup.setName("backup");
				backup.setTitle("backup");
				backup.setPath("/system/devices/backup");
				backup.setLocation(jcrService.getBackup());
				try {
					jcrService.addOrUpdate(backup);
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
				}
			}
			if(!jcrService.nodeExsits("/system/devices/default")) {
				Device home = new Device(jcrService.getDevice());
				home.setName("backup");
				home.setTitle("backup");
				home.setPath("/system/devices/backup");
				home.setLocation(jcrService.getDevice());
				try {
					jcrService.addOrUpdate(home);
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
				}			
			}			
		}

		ImageUtil.gpioMode("out");
		ImageUtil.HDDOn();
/*		try {
			ImageUtil.HDDSleep();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}*/
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
	protected String getDateTime() {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
		return sf.format(now);
		
		
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
    public void serveResource(HttpServletRequest request,HttpServletResponse response, File file,String contentType) throws Exception {
        
    	if (response == null || request == null) {
            return;
        }
		ImageUtil.HDDOn();
        if (!file.exists()) {
            logger.error("File doesn't exist at URI : {}", file.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Long length = file.length();
        String fileName = file.getParent()+"/"+file.getName();
        long lastModified = file.lastModified();
        if(contentType==null) {
        	Path path = FileSystems.getDefault().getPath(file.getParentFile().getAbsolutePath(), fileName);
        	contentType = Files.probeContentType(path);
        }
        // Validate request headers for caching ---------------------------------------------------

        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, fileName)) {
            response.setHeader("ETag", fileName); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setHeader("ETag", fileName); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // Validate request headers for resume ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !HttpUtils.matches(ifMatch, fileName)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        // Validate and process range -------------------------------------------------------------

        // Prepare some variables. The full Range represents the complete file.
        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<Range>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(fileName)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = Range.sublong(part, 0, part.indexOf("-"));
                    long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    // Add range.                    
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        // Prepare and initialize response --------------------------------------------------------

        // Get content type by file name and set content disposition.
        String disposition = "inline";

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = "application/octet-stream";
        } else if (!contentType.startsWith("image")) {
            // Else, expect for images, determine content disposition. If content type is supported by
            // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
            String accept = request.getHeader("Accept");
            disposition = accept != null && HttpUtils.accepts(accept, contentType) ? "inline" : "attachment";
        }
        logger.debug("Content-Type : {}", contentType);
        // Initialize response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        logger.debug("Content-Disposition : {}", disposition);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", fileName);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

        // Send requested file (part(s)) to client ------------------------------------------------

        // Prepare streams.
        InputStream input = new FileInputStream(file);
        OutputStream output = response.getOutputStream();
        try {

            if (ranges.isEmpty() || ranges.get(0) == full) {

                // Return full file.
                logger.debug("Return full file");
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + full.start + "-" + full.end + "/" + full.total);
                response.setHeader("Content-Length", String.valueOf(full.length));
                Range.copy(input, output, length, full.start, full.length);

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                logger.debug("Return 1 part of file : from ({}) to ({})", r.start, r.end);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Copy single part range.
                Range.copy(input, output, length, r.start, r.length);

            } else {

                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Cast back to ServletOutputStream to get the easy println methods.
                ServletOutputStream sos = (ServletOutputStream) output;

                // Copy multi part range.
                for (Range r : ranges) {
                    logger.info("Return multi part of file : from ({}) to ({})", r.start, r.end);
                    // Add multipart boundary and header fields for every range.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Type: " + contentType);
                    sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                    // Copy single part range of multi part range.
                    Range.copy(input, output, length, r.start, r.length);
                }

                // End with multipart boundary.
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }
        }finally{
        	input.close();
        	output.close();
    		ImageUtil.HDDOff();
        }

    }

  
  private static class Range {
        long start;
        long end;
        long length;
        long total;
        /**
         * Construct a byte range.
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

        public static long sublong(String value, int beginIndex, int endIndex) {
            String substring = value.substring(beginIndex, endIndex);
            return (substring.length() > 0) ? Long.parseLong(substring) : -1;
        }

        private static void copy(InputStream input, OutputStream output, long inputSize, long start, long length) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                // Write full range.
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            } else {
                input.skip(start);
                long toRead = length;

                while ((read = input.read(buffer)) > 0) {
                    if ((toRead -= read) > 0) {
                        output.write(buffer, 0, read);
                        output.flush();
                    } else {
                        output.write(buffer, 0, (int) toRead + read);
                        output.flush();
                        break;
                    }
                }
            }
        }
    }
  
	protected Device getDevice() {
		Device device = new Device();		
		if(jcrService.getDevice()!=null && !"".equals(jcrService.getDevice())) {

			if(!jcrService.nodeExsits("/system/devices")) {
				try {
					jcrService.addNodes("/system/devices", "nt:unstructured", getUsername());
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
				}
			}
			
			if(jcrService.nodeExsits("/system/devices/default")) {
				try {
					device = (Device)jcrService.getObject("/system/devices/default");
					if(device.getLocation().equals(jcrService.getDevice())) {
						jcrService.updatePropertyByPath(device.getPath(), "location", jcrService.getDevice());
					}
					return device;
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
				}
				
			}
			device.setPath("/system/devices/default");
			device.setTitle("default");
			device.setLocation(jcrService.getDevice());
			try {
				File dir = new File(jcrService.getDevice());
				if(!dir.exists())
					dir.mkdirs();
				device.setStatus("enabled");
				jcrService.addOrUpdate(device);
				
				return device;
			} catch (RepositoryException e) {
				logger.error("device:"+e.getMessage());
			}				


		}
		return device;
	}

	  
		protected Device getBackup() {
			Device device = new Device();		
			if(jcrService.getDevice()!=null && !"".equals(jcrService.getDevice())) {

				if(!jcrService.nodeExsits("/system/devices")) {
					try {
						jcrService.addNodes("/system/devices", "nt:unstructured", getUsername());
					} catch (RepositoryException e) {
						logger.error(e.getMessage());
					}
				}
				
				if(jcrService.nodeExsits("/system/devices/backup")) {
					try {
						device = (Device)jcrService.getObject("/system/devices/backup");
						if(device.getLocation().equals(jcrService.getBackup())) {
							jcrService.updatePropertyByPath(device.getPath(), "location", jcrService.getBackup());
						}
						return device;
					} catch (RepositoryException e) {
						logger.error(e.getMessage());
					}
					
				}
				device.setPath("/system/devices/backup");
				device.setTitle("backup");
				device.setLocation(jcrService.getBackup());
				try {
					File dir = new File(jcrService.getBackup());
					if(!dir.exists())
						dir.mkdirs();
					device.setStatus("enabled");
					jcrService.addOrUpdate(device);
					
					return device;
				} catch (RepositoryException e) {
					logger.error("device:"+e.getMessage());
				}				


			}
			return device;
		}
	  	
    protected static class HttpUtils {

        /**
         * Returns true if the given accept header accepts the given value.
         * @param acceptHeader The accept header.
         * @param toAccept The value to be accepted.
         * @return True if the given accept header accepts the given value.
         */
        public static boolean accepts(String acceptHeader, String toAccept) {
            String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
            Arrays.sort(acceptValues);

            return Arrays.binarySearch(acceptValues, toAccept) > -1
                    || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                    || Arrays.binarySearch(acceptValues, "*/*") > -1;
        }

        /**
         * Returns true if the given match header matches the given value.
         * @param matchHeader The match header.
         * @param toMatch The value to be matched.
         * @return True if the given match header matches the given value.
         */
        public static boolean matches(String matchHeader, String toMatch) {
            String[] matchValues = matchHeader.split("\\s*,\\s*");
            Arrays.sort(matchValues);
            return Arrays.binarySearch(matchValues, toMatch) > -1
                    || Arrays.binarySearch(matchValues, "*") > -1;
        }
}
	
}
