package com.intelizign.career.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelizign.career.authentication.JWTService;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.exception.TokenRefreshException;
import com.intelizign.career.request.LoginRequest;
import com.intelizign.career.response.LoginResponse;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.response.TokenRefreshResponse;
import com.intelizign.career.service.AuthService;
import com.intelizign.career.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private JWTService jwtUtils;

	@Autowired
	private Environment env;

	private String token = null;

	Logger Logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/login")
	public ResponseEntity<Object> authenticateUser(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
		try {
			LoginResponse loginResponse = authService.verifyUser(request, httpRequest, response);
			if(loginResponse == null) {
				return ResponseHandler.generateResponse("Login Response is empty",false, HttpStatus.OK, null);
			}
			return ResponseHandler.generateResponse("Login Successfully", true, HttpStatus.OK, loginResponse);
		
		} catch (DataIntegrityViolationException ex) {
			
			Logger.error("Try Refreshing the token" + ex.getMessage());
			throw new CustomExceptions.DuplicateResourceException("Try Refreshing the token");
 
		} catch (Exception ex) {
			Logger.error("Could not Login User " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}

	@PostMapping("/refreshToken")
	public ResponseEntity<Object> refreshtoken(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Extract refresh token from cookies
			String refreshToken = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("refreshtoken"))
					.findFirst().map(Cookie::getValue).orElse(null);

			if (refreshToken == null) {
				throw new TokenRefreshException(null, "Refresh token is missing!");
			}

			return refreshTokenService.findByToken(refreshToken).map(refreshTokenService::verifyExpiration)
					.map(token -> {
						String newAccessToken;
						String email;
						String role;

						// Check if the refresh token belongs to an Admin
						if (token.getAdmin() != null) {
							email = token.getAdmin().getEmail();
							role = token.getAdmin().getRole().toString();
						}
						// Check if the refresh token belongs to a Recruiter
						else if (token.getRecruiter() != null) {
							email = token.getRecruiter().getEmail();
							role = token.getRecruiter().getRole().toString();
						} else {
							throw new TokenRefreshException(refreshToken, "Invalid refresh token!");
						}

						// Generate new access token
						newAccessToken = jwtUtils.generateToken(email, role);

						// Create cookies for the new token and refresh token
						ResponseCookie tokenCookie = ResponseCookie.from("token", newAccessToken).httpOnly(false)
								.secure(true).domain(env.getProperty("career.cookies.allow.domain")).path("/")
								.maxAge(7 * 24 * 60 * (long) 60) // 7 days
								.build();

						ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshtoken", refreshToken)
								.httpOnly(false).secure(true).domain(env.getProperty("career.cookies.allow.domain"))
								.path("/").maxAge(7 * 24 * 60 * (long) 60) // 7 days
								.build();

						// Add cookies to response headers
						response.addHeader("Set-Cookie", tokenCookie.toString());
						response.addHeader("Set-Cookie", refreshTokenCookie.toString());

						return ResponseHandler.generateResponse("Token refreshed successfully", true, HttpStatus.OK,
								new TokenRefreshResponse(newAccessToken, refreshToken));
					}).orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

		} catch (Exception e) {
			Logger.error("Internal Server Error while refreshing token: {}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/logout")
	public ResponseEntity<Object> logoutUser(HttpServletResponse response, HttpServletRequest request) {
		try {
			if (request.getCookies() != null) {
				token = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("refreshtoken")).findFirst()
						.map(Cookie::getValue).orElse(null);

				refreshTokenService.deletetoken(token);
			}
			ResponseCookie tokencookie = ResponseCookie.from("token", null).httpOnly(false).secure(true)
					.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(0).build();

			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", null).httpOnly(false).secure(true)
					.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(0).build();

			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());

			return ResponseHandler.generateResponse("Logout successful!", true, HttpStatus.OK, null);

		} catch (Exception ex) {

			Logger.error("While logout : {}.", ex.getMessage());
			ResponseCookie tokencookie = ResponseCookie.from("token", null).httpOnly(false).secure(true)
					.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(0).build();

			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", null).httpOnly(false).secure(true)
					.domain(env.getProperty("career.cookies.allow.domain")).path("/").maxAge(0).build();
			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());

			return ResponseHandler.generateResponse("Logout not successful!", true, HttpStatus.OK, null);
		}
	}
}