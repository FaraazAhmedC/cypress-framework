package com.intelizign.career.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.exception.TokenRefreshException;
import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.model.RefreshToken;
import com.intelizign.career.repository.AdminRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

	@Value("${intelizign.career.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	RecruiterRepository	recruiterRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	/*
	 * Refresh Token creation with userId
	 */
	public RefreshToken createRefreshToken(Long userId, String userType) {
	    RefreshToken refreshToken = new RefreshToken();
	    
	    if ("ADMIN".equalsIgnoreCase(userType)) {
	        Admin admin = adminRepository.findById(userId)
	                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Admin not found with ID: " + userId));
	        refreshToken.setAdmin(admin); // Use setAdmin() for Admin
	    } else if ("RECRUITER".equalsIgnoreCase(userType)) {
	        Recruiter recruiter = recruiterRepository.findById(userId)
	                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recruiter not found with ID: " + userId));
	        refreshToken.setRecruiter(recruiter); // Use setRecruiter() for Recruiter
	    } else {
	        throw new IllegalArgumentException("Invalid user type: " + userType);
	    }

	    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
	    refreshToken.setToken(UUID.randomUUID().toString());

	    return refreshTokenRepository.save(refreshToken);
	}

	/*
	 * Verify the expiration time of Refresh Token
	 */
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

	@Transactional
	public void deletetoken(String token) {
		System.out.println(token);
		refreshTokenRepository.deleteByToken(token);
	}
	
	@Transactional
	public int deleteByUserId(Long userId, String userType) {
	    if ("ADMIN".equalsIgnoreCase(userType)) {
	        Admin admin = adminRepository.findById(userId)
	                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Admin not found with ID: " + userId));
	        return refreshTokenRepository.deleteByAdmin(admin);
	    } else if ("RECRUITER".equalsIgnoreCase(userType)) {
	        Recruiter recruiter = recruiterRepository.findById(userId)
	                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recruiter not found with ID: " + userId));
	        return refreshTokenRepository.deleteByRecruiter(recruiter);
	    } else {
	        throw new IllegalArgumentException("Invalid user type: " + userType);
	    }
	}

}
