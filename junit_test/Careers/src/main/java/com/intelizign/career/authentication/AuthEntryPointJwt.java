package com.intelizign.career.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger Logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
	private String messageData = "message";	
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		Logger.error("Unauthorized error: {}", authException.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_OK);

		final Map<String, Object> body = new HashMap<>();
		body.put("statuscode", HttpServletResponse.SC_OK);
		body.put("status", false);
		if (authException.getMessage().equalsIgnoreCase("Bad credentials")) {
			body.put(messageData, "Invalid Credentials");
		} else {
			body.put(messageData, "Session Expired");
		}
		body.put("error", "Unauthorized");
		body.put("path", request.getServletPath());

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);

	}
}
