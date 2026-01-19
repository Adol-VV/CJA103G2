package com.momento.member.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception{
		
		HttpSession session = req.getSession();
		
		Object member = session.getAttribute("loginMember");
		
		if( member != null ) {
			return true;
		}
		
		res.sendRedirect("/member/login");
		return false;
	}
}
