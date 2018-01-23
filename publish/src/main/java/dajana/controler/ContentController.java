package dajana.controler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dajana.utils.JwtUtil;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.itextpdf.text.pdf.BarcodeQRCode;

import dajana.model.Page;
import dajana.model.User;

@Controller
public class ContentController {

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";
	@Value("${publish.home}")
	private String publish_home = "/home/publish";
	@Value("${sso.url}")
	private String sso_ur = "http://sso.dajana.ca";
	@Value("${base.url}")
	private String baseUrl = "http://117.26.129.34:8888";	
	@Value("${cacheTime.page}")
	private long cacheTime = 7200000;
	@Value("${base.log}")
	private String logBase = "/var";	
	@Value("${passcode.fail}")
	private String passcode_fial ="The passcode is incorrect";
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@RequestMapping(value = {"/","/content"})
	public String welcome(HttpServletRequest request, HttpServletResponse response,Map<String, Object> model) {
		model.put("message", this.message);

        String site = request.getServerName();
        String [] domains = site.split(".");
        String subdomain = "home";
        if(domains.length>2) {
        	subdomain = domains[0];
        }

		logger.info(site+"/content/"+subdomain);
		
		try {
			response.sendRedirect("/content/"+subdomain+".html");
		} catch (IOException e) {
			logger.error(e);
			return "error";
		}
		return "welcome";
	}

	@RequestMapping(value = {"/log/**/*","/log/*","/logs/**/*","/logs/**/*"})
	public String logs(HttpServletRequest request, HttpServletResponse response,Map<String, Object> model) {
		
		String paths;
		FileInputStream in = null;
		try {
			paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			File file =new File(logBase+paths);
			if(file.exists()) {
				in = new FileInputStream(file);
				response.setContentLength((int)file.length());
				IOUtils.copy(in, response.getOutputStream());
				in.close();
				return null;
			}else {
				logger.error("log not found:"+paths);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}


		return "error";
	}
	
	@RequestMapping("/content/**")
	public String content(HttpServletRequest request, HttpServletResponse response,Map<String, Object> model) {

		String paths;
		FileInputStream in = null;
		try {
			paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".html")[0];
			Page currentpage = null;
			if(paths.endsWith(".html")) 
				currentpage = getPage(path);
			if( currentpage == null){
				File file =new File(publish_home+paths);
				if(file.exists()) {
					in = new FileInputStream(file);
					response.setContentLength((int)file.length());
					IOUtils.copy(in, response.getOutputStream());
					in.close();
					return null;
				}else {
					logger.error("Content not found:"+path);
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return null;
				}
			}
			model.put("page", currentpage);
			String navigation = currentpage.getNavigation(),breadcrumb=currentpage.getBreadcrumb(),content=currentpage.getContent(),menuPath=currentpage.getMenuPath();
			String pagePasscode = currentpage.getPasscode();
			if(pagePasscode !=null && !"".equals(pagePasscode)) {
				if("true".equals(currentpage.getSecured())) {
					pagePasscode = JwtUtil.decode(pagePasscode);
					currentpage.setContent(JwtUtil.decode(currentpage.getContent()));
				}
				HttpSession session = request.getSession();
				String parent_path = (String)session.getAttribute(pagePasscode);
				String passcode = (String)session.getAttribute(parent_path);				
				if(passcode==null || parent_path==null || !passcode.equals(pagePasscode)) {
					model.put("path", path);
					model.put("title", currentpage.getTitle());
					model.put("passcode", currentpage.getPasscode());
					return "pass";
				}
			}

			if(currentpage.getRedirectTo() != null && !"".equals(currentpage.getRedirectTo())) {
				return "redirect:"+currentpage.getRedirectTo();
			}	
			navigation = FileUtils.readFileToString(new File(this.publish_home+menuPath+"/navimenu.html"),"UTF-8");
			if("true".equals(currentpage.getShowLeftmenu())) {
				File leftmenu = new File(this.publish_home+path+"/leftmenu.html");
				if(leftmenu.exists()) {
					model.put("leftmenu", FileUtils.readFileToString(leftmenu,"UTF-8"));
				}else {
					Document doc = Jsoup.parse(new URL(baseUrl+"/content/leftmenu.html?path="+path), 5000);
					model.put("leftmenu",doc.body().html());					
				}
			}
			currentpage.setNavigation(navigation);
		} catch (UnsupportedEncodingException e) {
			return "error500";
		} catch (FileNotFoundException e) {
			return "error404";
		} catch (IOException e) {
			return "error500";
		}finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	
		return "content";
	}
	
	@RequestMapping(value = {"/content/{site}.passcode","/content/{site}.passcode","/content/{site}/**/*.passcode","/content/{site}/*.passcode"}, method = RequestMethod.POST)
	public String passcode(@PathVariable String site, String passcode, Map<String, Object> model,HttpServletRequest request, HttpServletResponse response) throws Exception  {
		try {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".passcode")[0];
			HttpSession session = request.getSession();
			Page currentpage = new Page();
			Gson gson = new Gson();
			File json = new File(publish_home+path+".json");			
			InputStream is = new FileInputStream(json);
			currentpage = gson.fromJson(new InputStreamReader(is,"UTF-8"), Page.class);
			is.close();
			model.put("username", session.getAttribute("username"));
			model.put("site", site);
			model.put("path", path);
			model.put("title", currentpage.getTitle());
			if(currentpage.getPasscode() !=null && !"".equals(currentpage.getPasscode())) {
				String pagePasscode = currentpage.getPasscode();
				if("true".equals(currentpage.getSecured())) {
					pagePasscode = JwtUtil.decode(pagePasscode);
				}
				if(passcode==null || !passcode.equals(pagePasscode)) {
					model.put("message", this.passcode_fial);
					return "pass";
				}else {
					session.setAttribute(path, pagePasscode);
					session.setAttribute(pagePasscode, path);
					response.sendRedirect(path+".html");
					return null;
				}
			}else {
				response.sendRedirect(path+".html");
				return null;
			}
	
		} catch (UnsupportedEncodingException e) {
			throw new Exception("\u8DEF\u5F84\u51FA\u9519!");
		} 

	}	
	
	private Page getPage(String path) {
		InputStream is = null;
		Page page = new Page();
		Gson gson = new Gson();
		File json = new File(publish_home+path+".json");
		String result="getPage:"+path;
		if(!json.exists()) {
			page = readPage(path);
		}else {
			result = "gjson exists"+path+".json";
			logger.debug(result);
			try {
				is = new FileInputStream(json);
				page = gson.fromJson(new InputStreamReader(is,"UTF-8"), Page.class);
				if(page.getBaseUri()==null || "".equals(page.getBaseUri())) {
					page.setBaseUri(baseUrl);
				}
				Date now = new Date();
				if(page.getLastExpired()!=null && now.getTime() - page.getLastExpired().getTime() > cacheTime) {
					page = readPage(path);
				}
			} catch (FileNotFoundException e) {
				logger.error(e);
			} catch (JsonSyntaxException e) {
				logger.error(e);
			} catch (JsonIOException e) {
				logger.error(e);;
			} catch (UnsupportedEncodingException e) {
				logger.error(e);;
			}finally {
				if(is != null)
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e);;
					}
			}

			return page;
		}	

		return null;
	}
	
	@RequestMapping(value = {"/content/*.shr","/content/**/*.shr"}, method = RequestMethod.GET)
	public String share(String path,Map<String,Object> model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		String url = request.getRequestURL().toString().replace(".shr", ".qrb");;
		if(path==null)
			path = url;
		model.put("qrpath", path);
		
		return "share";
	}
	
	@RequestMapping(value = {"/content/*.qrb","/content/**/*.qrb"}, method = RequestMethod.GET)
	public @ResponseBody String getQRBarcode(String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url = request.getRequestURL().toString().replace(".qrb", ".html");;
		if(path==null)
			path = url;

        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(path, 360, 360, null);
        java.awt.Image awtImage = barcodeQRCode.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bImage= new BufferedImage(360, 360, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();
        response.setContentType("image/jpg");
        ImageIO.write(bImage, "jpg", response.getOutputStream());
		
		return null;
	}	
	
	private Page readPage(String path) {
		String paths[] = path.split("/");
		String result = "";
		InputStream is = null;
		Page page = new Page();
		User user = null;
		Gson gson = new Gson();
		File json = new File(publish_home+path+".json");
		File juser = new File(publish_home+"/content/"+paths[2]+"/user.json");
		try {
			if(juser.exists()) {
				is = new FileInputStream(juser);
				user = gson.fromJson(new InputStreamReader(is,"UTF-8"),User.class);
			}else {
				user = readUser(path);
			}
			
			if(user !=null) {
				result = "get copyURLToFile:"+"http://"+user.getHost()+":"+user.getPort()+path+".json";
				URL url = new URL("http://"+user.getHost()+":"+user.getPort()+path+".json");
				URLConnection connection = url.openConnection();
				connection.setReadTimeout(2000);
				is = connection.getInputStream();
				page = gson.fromJson(new InputStreamReader(is,"UTF-8"), Page.class);
				result = "set ABSURL";
				page.setBaseUri("http://"+user.getHost()+":"+user.getPort());
				Document doc = Jsoup.parse(page.getContent());
				doc.setBaseUri("http://"+user.getHost()+":"+user.getPort());
				for(Element e:doc.select("img")) {
					e.attr("src", e.absUrl("src"));
				}
				for(Element e:doc.select("a.wb-lbx")) {
					e.attr("href", e.absUrl("href"));
				}
				page.setLastExpired(new Date());
				page.setContent(doc.body().html());
				String jsonPage = gson.toJson(page);
				//bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json)));
				BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json),"UTF-8"));
				bufferWriter.write(jsonPage);
				bufferWriter.close(); 
				if("true".equals(page.getShowLeftmenu())) {
					doc = Jsoup.parse(new URL("http://"+user.getHost()+":"+user.getPort()+"/content/leftmenu.html?path="+path), 5000);
					File leftmenu = new File(publish_home+path+"/leftmenu.html");
					bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(leftmenu),"UTF-8"));
					bufferWriter.write(doc.body().html());
					bufferWriter.close(); 
				}
				
				return page;
				//logger.info(result);
				//org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://"+uinfos[1]+":"+uinfos[2]+path+".json"), json,1000,1000);
			}
		} catch (MalformedURLException e) {
			logger.error(result+":"+e);
		} catch (IOException e) {
			logger.error(result+":"+e);
		}finally {
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);;
				}
		}	
		logger.debug(result);
		return null;
	}
	
	private User readUser(String path) {
		String paths[] = path.split("/");
		String result = "";
		InputStream is = null;
		User user = new User();
		Gson gson = new Gson();
		File juser = new File(publish_home+"/content/"+paths[2]+"/user.json");
		try {
			String userinfo = org.apache.commons.io.IOUtils.toString(new URL(this.sso_ur+"/uinfo/"+paths[2]), "UTF-8");
			result = "uinfo="+userinfo;
			String uinfos[] = userinfo.split("/");
			
			if(paths[2].equals(uinfos[0])) {
				user.setUserName(uinfos[0]);
				user.setHost(uinfos[1]);
				user.setPort(uinfos[2]);
				user.setHostIp(uinfos[3]);
				String jsonUsr = gson.toJson(user);
				//bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json)));
				BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(juser),"UTF-8"));
				bufferWriter.write(jsonUsr);
				bufferWriter.close(); 
				//navimenu
				File navimenu = new File(publish_home+"/content/"+paths[2]+"/navimenu.html");
				URL url = new URL("http://"+user.getHost()+":"+user.getPort()+"/content/"+paths[2]+"/navimenu.html");
				URLConnection connection = url.openConnection();
				connection.setReadTimeout(2000);
				is = connection.getInputStream();
				org.apache.commons.io.IOUtils.copy(is, new FileOutputStream(navimenu));
				is.close();
				return user;
			}
		} catch (MalformedURLException e) {
			logger.error(result+":"+e);
		} catch (IOException e) {
			logger.error(result+":"+e);
		}finally {
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);;
				}
		}	
		logger.debug(result);
		return null;
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
}
