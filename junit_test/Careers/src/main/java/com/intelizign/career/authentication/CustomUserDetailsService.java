package com.intelizign.career.authentication;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.AdminRepository;
import com.intelizign.career.repository.RecruiterRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private  AdminRepository adminRepository;

	@Autowired
	private  RecruiterRepository recruiterRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<Admin> admin = adminRepository.findByEmail(email);
		if (admin.isPresent()) {
			return new CustomUserDetails(admin.get());
		}

		Optional<Recruiter> recruiter = recruiterRepository.findByEmail(email);
		if (recruiter.isPresent()) {
			return new CustomUserDetails(recruiter.get());

		}

		throw new UsernameNotFoundException("User not found with email: " + email);  
	}
}
