package com.onepiece.config;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
@Order(1)
public class AuthorizationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(false);
		String requestURI = request.getRequestURI();
		
		
		if (requestURI.equals("/manga/reviewManga") || requestURI.equals("/manga/allreviews")) {
	        if (session == null || (session.getAttribute("adminID") == null && session.getAttribute("studentEmail") == null)) {
	            response.sendRedirect("/accessDenied");
	            return;
	        }
	    }else

		if (isUnrestrictedPath(requestURI)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}else

		if (isAdminRestrictedPath(requestURI)) {
			if (session == null || session.getAttribute("adminID") == null) {
				response.sendRedirect("/accessDenied/admin");
				return;
			}
		}else

		// Student-specific paths
		if (isStudentRestrictedPath(requestURI)) {
			if (session == null || session.getAttribute("studentEmail") == null) {
				response.sendRedirect("/accessDenied/student");
				return;
			}
		}
		

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private boolean isUnrestrictedPath(String uri) {
		return uri.equals("/admin/login") || uri.equals("/admin/logout") || uri.equals("/student/login")
				|| uri.equals("/student/logout") || uri.equals("/admin/signup") || uri.equals("/student/signup");

	}

	private boolean isAdminRestrictedPath(String uri) {
		return uri.startsWith("/admin/") || uri.startsWith("/manga/addManga") || uri.startsWith("/manga/edit/")
				|| uri.startsWith("/manga/deleteManga") || uri.startsWith("/manga/adminbrowse")
				|| uri.startsWith("/student/details") || uri.startsWith("/admin/adminProfile")
				|| uri.startsWith("/downloadMangaDetails/xls") || uri.startsWith("/password/update/admin");
	}

	private boolean isStudentRestrictedPath(String uri) {
		return uri.startsWith("/manga/borrowManga") || uri.startsWith("/student/dashboard")
				|| uri.equals("/student/borrowedMangas") || uri.equals("/student/successpage")
				|| uri.startsWith("/manga/browseManga") || uri.startsWith("/student/returnManga")
				|| uri.startsWith("/student/studentProfile") || uri.startsWith("/downloadBorrowedMangas/pdf")
				|| uri.startsWith("/password/update/student") || uri.startsWith("/student/verifyPassword");
	}
}
