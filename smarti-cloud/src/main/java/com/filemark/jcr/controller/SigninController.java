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
import com.filemark.sso.CookieUtil;
import com.filemark.sso.JwtUtil;


@Controller
public class SigninController extends BaseController{
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "dajanaSigningKey";

	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(String redirect, HttpServletRequest request, HttpServletResponse response) {

        String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
        CookieUtil.clear(response, jwtTokenCookieName,domain);
        request.getSession().invalidate();
		
    	return "signin";		
	}

	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public void register(String redirect, HttpServletRequest request, HttpServletResponse response) {
	}
	
	private String getRolePrefix() {
		
		return "ROLE_";
	}

}
