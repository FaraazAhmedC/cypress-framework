package com.intelizign.career.authentication;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals("ROLE_ADMIN")) {
				setDefaultTargetUrl("/admin/dashboard");  // Redirect Admin
				break;
			} else if (authority.getAuthority().equals("ROLE_RECRUITER")) {
				setDefaultTargetUrl("/recruiter/dashboard");  // Redirect Recruiter
				break;
			}
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
