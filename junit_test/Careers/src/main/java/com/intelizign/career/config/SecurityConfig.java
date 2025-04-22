package com.intelizign.career.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.intelizign.career.authentication.AuthEntryPointJwt;
import com.intelizign.career.authentication.CustomUserDetailsService;
import com.intelizign.career.authentication.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(customizer -> customizer.disable()).
				authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**","/admin/createAdmin", "/applicant/{jobId}/apply", "/recruiter/activate**","/success.html").permitAll()
                        .anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin(form -> form.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://careers-dev.izserver24.in")); // Adjust as needed
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("*"));
	    configuration.setAllowCredentials(true);
	    configuration.setMaxAge(3600L);
 
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	 @Bean
//	    public CorsConfigurationSource corsConfigurationSource() {
//	        CorsConfiguration configuration = new CorsConfiguration();
//	        configuration.setAllowedOrigins(List.of("https://careers-dev.izserver24.in"));
//	        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//	        configuration.setAllowedHeaders(List.of("*"));
//	        configuration.setAllowCredentials(true);
//	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	        source.registerCorsConfiguration("/**", configuration);
//	        return source;
//	    }
}