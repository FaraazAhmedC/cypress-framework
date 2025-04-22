package com.intelizign.career.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.intelizign.career.authentication.JWTService;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.model.RefreshToken;
import com.intelizign.career.repository.AdminRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.repository.RefreshTokenRepository;
import com.intelizign.career.request.LoginRequest;
import com.intelizign.career.response.LoginResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private RecruiterRepository recruiterRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private Environment env;
	
	public LoginResponse verifyUser(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
		Optional<Admin> admin = adminRepository.findByEmail(request.getEmail());
		Optional<Recruiter> recruiter = recruiterRepository.findByEmail(request.getEmail());
 
		if (admin.isPresent()) {
			return authenticateUser(httpRequest, admin.get().getEmail(), request.getPassword(), "ADMIN", admin.get().getId(),
					admin.get().getName(),admin.get().getFirstName(), admin.get().getLastName(),response);
		} else if (recruiter.isPresent()) {
			return authenticateUser(httpRequest, recruiter.get().getEmail(), request.getPassword(), "RECRUITER",
					recruiter.get().getId(), recruiter.get().getName(), recruiter.get().getFirstName(), recruiter.get().getLastName(),response);
		} else {
			throw new CustomExceptions.DuplicateResourceException("Not a valid User");
		}
	}
 
	private LoginResponse authenticateUser(HttpServletRequest httpRequest, String email, String password, String role, Long id, String name,String firstName,String lastName,
			HttpServletResponse response) {
		
		Cookie[] cookies = httpRequest.getCookies();		
		if(cookies == null && refreshTokenRepository.existsByUserID(id)) {			
			refreshTokenService.deleteByUserId(id, role);
			throw new CustomExceptions.ResourceNotFoundException("Try logging in again");
		}
		
		Authentication authentication = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));
 
			SecurityContextHolder.getContext().setAuthentication(authentication);
 
			String token = jwtService.generateToken(email, role);
			RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(id, role);
			ResponseCookie tokencookie = ResponseCookie.from("token", token).httpOnly(false).secure(true)
					.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(7 * 24 * 60 * (long) 60)
					.build();
			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", newRefreshToken.getToken())
					.httpOnly(false).secure(true).domain(env.getProperty("career.cookies.allow.domain")).path("/")
					.maxAge(7 * 24 * 60 * (long) 60).build();
			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());
 
			return new LoginResponse(id, name, firstName,lastName,email, role,token);
	}
}