package com.intelizign.career.authentication;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private Environment env;

	private String token = null;
	
	public JwtFilter(JWTService jwtService) {
		this.jwtService = jwtService;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			token = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("token")).findFirst()
					.map(Cookie::getValue).orElse(null);

			String username = jwtService.extractUserName(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if (token != null && jwtService.validateToken(token, userDetails)) {
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				// Refresh token
				String role = jwtService.extractRole(token);
				String newToken = jwtService.generateToken(username, role);

				ResponseCookie tokencookie = ResponseCookie.from("token", newToken).httpOnly(false).secure(true)
						.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(7 * 24 * 60 * 60).build();

				response.addHeader("Set-Cookie", tokencookie.toString());
			} 
		} catch (Exception ex) {
			LOGGER.error("Cannot set User Authentication in filter method" + ex.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
