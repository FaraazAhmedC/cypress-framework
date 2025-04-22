package com.intelizign.career.authentication;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JWTService {

	@Value("${intelizign.career.jwtSecret}")
	private String secretkey;

	@Value("${intelizign.career.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	private Environment env;

	Logger logger = LoggerFactory.getLogger(JWTService.class);

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, "token");
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from("token", null)
				.domain(env.getProperty("career.cookies.allow.domain")).path("/").build();
		return cookie;
	}

	// email is passed in the place of username
	public String generateToken(String username, String role) {
		return Jwts.builder().claim("role", role).subject(username).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(getKey()).compact();
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// extract the username from jwt token
	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			final String userName = extractUserName(token);
			if (userName.equals(userDetails.getUsername()) && !isTokenExpired(token))
				return true;

		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}