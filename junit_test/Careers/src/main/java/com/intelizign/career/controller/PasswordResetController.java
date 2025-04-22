package com.intelizign.career.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.PasswordResetToken;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.PasswordResetTokenRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.response.ResponseHandler;

@RestController
@PreAuthorize("hasAuthority('RECRUITER')")
@RequestMapping("/password")
public class PasswordResetController {
	
	@Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@PostMapping("/reset")
	public ResponseEntity<Object> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
		try {
			PasswordResetToken resetToken = tokenRepository.findByToken(token)
					.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Invalid or expired token"));

			// Check if token has expired
			if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
				throw new CustomExceptions.ResourceNotFoundException("Token has expired");
			}

			// Find the recruiter by email
			Recruiter recruiter = recruiterRepository.findByEmail(resetToken.getEmail())
					.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recruiter not found"));

			// Encode and update the password
			recruiter.setPassword(passwordEncoder.encode(newPassword));
			recruiterRepository.save(recruiter);

			// Delete the token after it has been used
			tokenRepository.delete(resetToken);

			return ResponseHandler.generateResponse("Password has been successfully reset", true, HttpStatus.OK, null);
		} catch (Exception e) {
			return ResponseHandler.generateResponse("Error occurred while resetting password", false, HttpStatus.BAD_REQUEST, null);
		}
	}

}
