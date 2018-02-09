package dajana.controler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dajana.UserRepository;
import dajana.model.Page;
import dajana.model.User;
import dajana.utils.CookieUtil;
import dajana.utils.JwtUtil;



@Controller
public class LoginController {
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "dajanaSigningKey";
	@Value("${login.fail}")
	private String login_error ="The password or username is incorrect";
	@Value("${user.exists}")
	private String user_exists ="User exists";
	@Value("${email_phone.empty}")
	private String email_phone_empty ="Email or phone can not be empty";
	@Value("${signup.success}")
	private String signup_success ="Signup successfully";	
	@Value("${page_title.signup}")
	private String page_title ="Signup";	
	@Value("${base.url}")
	private String baseUrl = "http://117.26.129.34:8888";	
	
	@Autowired
	private UserRepository userRepository;
	
	private static final Logger logger = LogManager.getLogger(LoginController.class);
	
    public LoginController() {
    }

    @RequestMapping(value = {"/myip/{uid}"}, method = RequestMethod.GET)
   	public @ResponseBody String myip(@PathVariable String uid,Model model,HttpServletRequest request, HttpServletResponse response) {
    	String myip="";
    	try {
			InetAddress ipAddr = InetAddress.getLocalHost();
	    	myip = getClientIpAddress(request);/*ipAddr.getHostAddress()+"="+request.getRemoteAddr()+"="+getPublicIpAddress()+"ip"+*/
	    	User user = (User)userRepository.findOne(uid);
	    	if(user !=null && !myip.equals(user.getHostIp())) {
	    		user.setHostIp(myip);
	    		userRepository.save(user);
	    	}
    	} catch (UnknownHostException e) {
    		myip ="error:UnknownHostException";
			logger.error(e.getMessage());
		} 

    	
		return myip;

    } 	

    @RequestMapping(value="/login",method = RequestMethod.GET)
    public String login(Model model,HttpServletRequest request, HttpServletResponse response,String redirect,String info, Integer loginCount) throws IOException{
        String token = JwtUtil.getSubject(request, jwtTokenCookieName, signingKey);
        if(token != null){
        	String authors[] = token.split("/");
        	User user = (User)userRepository.findOne(authors[2]);
    		if(user.getHost()!=null && !"".equals(user.getHost())) {
    			response.sendRedirect("http://"+(user.getHost()+(user.getPort()==null || "".equals(user.getPort())?"":":"+user.getPort()) +"/content/"+authors[2]+".html"));
    			return null;
    		}
    		response.sendRedirect("/site/editor.html?path=/content/" + authors[2]);     	
        	
        }
    	Page page = new Page();
    	if(loginCount==null) loginCount=0;
    	loginCount++;    	
    	model.addAttribute("j_username", "");
    	model.addAttribute("info", info);
    	model.addAttribute("page", page);
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	
 		
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String logon(HttpServletRequest request,HttpServletResponse httpServletResponse, String j_username, String j_password, String redirect,Integer loginCount, Model model) throws IOException {
    	User user = new User();
    	Page page = new Page();
    	String lastIp = getClientIpAddress(request);
    	if(loginCount==null) loginCount=0;
    	loginCount++;
    	model.addAttribute("page", page);   
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	

        if (j_username == null || !userRepository.exists(j_username.toLowerCase()) ){
            logger.info("error="+j_username);  
            model.addAttribute("error", login_error);
            return "login";
        }else {
        	
        	user = (User)userRepository.findOne(j_username.toLowerCase());
        	if(user.getSigningKey() ==null || user.getSigningKey().equals("")) user.setSigningKey("dajana.cn");
        	
        	if(!user.getPassword().equals(j_password)) {
      		
                model.addAttribute("error", login_error);
                return "login";        		
        	}
        	if(!lastIp.equals(user.getLastIp())) {
        		user.setLastIp(lastIp);
        		userRepository.save(user);
        	}

        }

		if(user.getPort()==null || "".equals(user.getPort())) {
			user.setPort("");
		}else {
			user.setPort(":"+user.getPort());
		}

		//HttpSession session = request.getSession(true);
		request.setAttribute("usersite", user.getHost());
		request.setAttribute("port", user.getPort());
		request.setAttribute("username", j_username);
		request.setAttribute("usertitle", user.getTitle()); 
		request.setAttribute("signingKey", user.getSigningKey());  
        String token_author = user.getHost()+"/"+user.getPort()+"/"+j_username+"/"+user.getTitle()+"/"+user.getSigningKey();
		
        String token = JwtUtil.generateToken(signingKey, token_author);
        String domain =(user.getHost()==null || "".equals(user.getHost()))? request.getServerName():user.getHost();
        //domain = domain.replaceAll(".*\\.(?=.*\\.)", "");
        CookieUtil.create(httpServletResponse, jwtTokenCookieName, token, false, -1, domain);
        logger.info("token="+token);
        if(redirect==null || "".equals(redirect) || "login".equals(redirect)) {
    		String content = "/content/"+user.getUserName();

    		if(user.getHost()!=null && !"".equals(user.getHost())) {
    			httpServletResponse.sendRedirect("http://"+(user.getHost()+(user.getPort()==null || "".equals(user.getPort())?"":":"+user.getPort()) + content+".html"));
    			return null;
    		}
			httpServletResponse.sendRedirect("/site/editor.html?path=" + content);

    		return null;
        }else {
			httpServletResponse.sendRedirect(redirect);

        	return null;
        }
    }
    @RequestMapping(value="/signup",method = RequestMethod.GET)
    public String signup(String host,Model model,HttpServletResponse httpServletResponse){
    	String imgs[] = {"shu","niu","fu","tu","long","she","ma","yang","hou","ji","gou","zhu"};
    	String ids[] = {"A0","A1","A2","B0","B1","B2","C0","C1","C2","D0","D1","D2"};
    	Page page = new Page();
    	page.setTitle("\u6CE8\u518C ");
    	
    	Random rnd = new Random();
    	for(int i = imgs.length - 1; i >0 ; i--) {
    		int index = rnd.nextInt(i);
    		String a = imgs[index];
    		String b = ids[index];
    		imgs[index] = imgs[i];
    		ids[index] = ids[i];
    		imgs[i] = a;
    		ids[i] = b;
    	}
    	User user = new User();
    	user.setHost(host);
    	model.addAttribute("user", user);
    	model.addAttribute("imgs", imgs);    
    	model.addAttribute("ids", ids);
    	model.addAttribute("page", page);    	
        return "signup";
    } 
    
    @RequestMapping(value="/signup",method = RequestMethod.POST)
    public String register(User user,Model model,HttpServletRequest request,HttpServletResponse httpServletResponse) {
    	if(userRepository.exists(user.getUserName().toLowerCase()))
    	{
    		model.addAttribute("user", user);
    		model.addAttribute("error",user.getUserName()+user_exists);
    	}else if((user.getEmail()==null || user.getEmail().equals("")) && (user.getPhoneNumber()==null || user.getPhoneNumber().equals(""))) {
    		model.addAttribute("user", user);
    		
    		model.addAttribute("error", email_phone_empty);
    	}else {
    		userRepository.findByEmail(user.getEmail());
        	String lastIp = request.getRemoteAddr();
    		String title =user.getTitle();// DjnUtils.Iso2Uft8(user.getTitle());
    		user.setTitle(title);
    		user.setLastIp(lastIp);
    		userRepository.save(user);
    		//String content = "/content/"+user.getUserName();
    		Page page = new Page();
    		page.setTitle(title);
    		model.addAttribute("page", page);  
    		model.addAttribute("info", signup_success);
    		model.addAttribute("j_username", user.getUserName());
    		return "login";
    	}

    	String imgs[] = {"shu","niu","fu","tu","long","she","ma","yang","hou","ji","gou","zhu"};
    	String ids[] = {"A0","A1","A2","B0","B1","B2","C0","C1","C2","D0","D1","D2"};
    	Page page = new Page();
    	//page.setTitle("&#27880;&#20876;");
    	page.setTitle(page_title);
    	Random rnd = new Random();
    	for(int i = imgs.length - 1; i >0 ; i--) {
    		int index = rnd.nextInt(i);
    		String a = imgs[index];
    		String b = ids[index];
    		imgs[index] = imgs[i];
    		ids[index] = ids[i];
    		imgs[i] = a;
    		ids[i] = b;
    	}
    	model.addAttribute("imgs", imgs);    
    	model.addAttribute("ids", ids);
    	model.addAttribute("page", page);    	

        return "signup";
    } 
    
	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(String redirect, HttpServletRequest request, HttpServletResponse response) {
			
    	return "signin";		
	} 
	
	@RequestMapping(value="/signin", method=RequestMethod.POST)
	public String signin(HttpServletRequest request,HttpServletResponse httpServletResponse, String j_username, String j_password, String redirect,Integer loginCount, Model model) {
			
    	return "signin";		
	}
	
    @RequestMapping("/logout")
    public String logout(String redirect,HttpServletRequest request,HttpServletResponse httpServletResponse) throws IOException {

        String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
        CookieUtil.clear(httpServletResponse, jwtTokenCookieName,domain);
        
        request.getSession().invalidate();
        
        if(redirect !=null && !"".equals(redirect))
        	httpServletResponse.sendRedirect(redirect);
        else
        	httpServletResponse.sendRedirect("/");;
        return null;
        
    }
    
	private String getRolePrefix() {
		
		return "ROLE_";
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


}
