package com.intelizign.career.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.AdminRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.request.RecruiterRequest;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class AdminService {

	@Autowired
	private RecruiterRepository recruiterRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private EmailService emailService;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	private static final Logger Logger = LoggerFactory.getLogger(RecruiterService.class);

	public Admin createAdmin(Admin admin) {
		Optional<Admin> isAdminExists = adminRepository.findByEmail(admin.getEmail());
		if (isAdminExists.isPresent()) {
			return null;
		} else {
			admin.setPassword(encoder.encode(admin.getPassword()));
			return adminRepository.save(admin);
		}
	}

	public Recruiter createRecruiter(RecruiterRequest recruiterRequest, Admin admin) throws MessagingException{
		if(recruiterRequest.getPassword().equals(recruiterRequest.getConfirmPassword())) {
			recruiterRequest.setPassword(encoder.encode(recruiterRequest.getPassword()));
			Recruiter recruiter = new Recruiter(recruiterRequest.getFirstName(), recruiterRequest.getLastName(), recruiterRequest.getEmail(), recruiterRequest.getMobile(), recruiterRequest.getLocation(), recruiterRequest.getPassword(), admin);
			Recruiter recruiterResponse = recruiterRepository.save(recruiter);
			
	        emailService.sendRecruiterCreationEmail(
	                recruiterResponse.getEmail(),
	                recruiterResponse.getFirstName() + " " + recruiterResponse.getLastName(),
	                recruiterRequest.getPassword() // Send the plain password (be cautious about this)
	        );
			
	        Logger.info("Activation email sent successfully to: {}", recruiter.getEmail());
			return recruiterResponse;
			
		} else {
			return null;
		}

	}

	public List<Admin> findAll() {
		return adminRepository.findAll();
	}

	public Admin update(Long id, Admin admin) {
		Admin currentAdminId = adminRepository.findById(id).get();
		if(currentAdminId== null) {
			return null;
		}		
		currentAdminId.setFirstName(admin.getFirstName());
		currentAdminId.setLastName(admin.getLastName());
		currentAdminId.setEmail(admin.getEmail());
		currentAdminId.setPassword(admin.getPassword());
		return adminRepository.save(currentAdminId);	
	}


	public Admin deleteSoft(Long id) {
		Admin currentAdminId = adminRepository.findById(id).get();
		if(currentAdminId== null) {
			return null;
		}
		currentAdminId.setActive(false);
		return adminRepository.save(currentAdminId);	
	}

}