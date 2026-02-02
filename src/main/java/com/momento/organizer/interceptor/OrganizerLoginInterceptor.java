package com.momento.organizer.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OrganizerLoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

		HttpSession session = req.getSession();

		Object organizer = session.getAttribute("loginOrganizer");

		if (organizer != null) {
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			res.setDateHeader("Expires", 0); // Proxies.
			return true;
		}

		res.sendRedirect("/organizer/login");
		return false;
	}
}
