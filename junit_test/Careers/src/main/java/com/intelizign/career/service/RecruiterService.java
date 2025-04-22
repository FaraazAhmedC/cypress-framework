package com.intelizign.career.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intelizign.career.exception.CustomExceptions.UserNotFoundException;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.RecruiterRepository;

import jakarta.transaction.Transactional;

@Service
public class RecruiterService {

	@Autowired
	private RecruiterRepository recruiterRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public String activateRecruiter(String email, String password, String oneTimeToken) {
		Recruiter recruiter = recruiterRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Recruiter not found"));

		recruiter.setPassword(password); // Save the new password (NOT ENCRYPTED)
		recruiter.setPassword(passwordEncoder.encode(recruiter.getPassword()));
		recruiterRepository.save(recruiter);

		return "Recruiter account activated successfully. You can now log in.";
	}

	public Recruiter createRecruiter(Recruiter recruiter) {
		return recruiterRepository.save(recruiter);
	}

	public Recruiter updateRecruiter(Long id, Recruiter recruiterDetails) {
		Recruiter recruiter = recruiterRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Recruiter not found with ID " + id));

		recruiter.setFirstName(recruiterDetails.getFirstName());
		recruiter.setLastName(recruiterDetails.getLastName());
		recruiter.setEmail(recruiterDetails.getEmail());
		recruiter.setMobile(recruiterDetails.getMobile());
		recruiter.setLocation(recruiterDetails.getLocation());
		return recruiterRepository.save(recruiter);
	}

	public void deleteRecruiterSoft(Long id) {
		Recruiter recruiter = recruiterRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Recruiter not found with ID " + id));
	    recruiter.setActive(false);
	    recruiterRepository.save(recruiter);
	}

}
