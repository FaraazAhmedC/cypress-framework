package com.intelizign.career.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{

	//private static final String ALLOWED_ORIGIN = "https://careers-dev.izserver24.in";
	private static final String ALLOWED_ORIGIN = "http://localhost:3000";
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedOrigins(ALLOWED_ORIGIN) 
		.allowedMethods("*")
		.allowedHeaders("*")
		.allowCredentials(true)
		.maxAge(3600);
	}
}