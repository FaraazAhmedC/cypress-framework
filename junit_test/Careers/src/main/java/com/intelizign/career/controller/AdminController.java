package com.intelizign.career.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.AdminRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.request.RecruiterRequest;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.service.AdminService;

import jakarta.validation.Valid;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private RecruiterRepository recruiterRepository;

	Logger Logger = LoggerFactory.getLogger(AdminController.class);

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PostMapping("/createAdmin")
	public ResponseEntity<Object> createAdmin(@Valid @RequestBody Admin admin) {
		try {
			Admin adminResponse = adminService.createAdmin(admin);
			if (adminResponse == null) {
				return ResponseHandler.generateResponse("Admin Creation Failed", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Admin Created", true, HttpStatus.OK, adminResponse);
			}
		} catch (Exception ex) {
			Logger.error("Admin is not created " + ex.getMessage());
			return ResponseHandler.generateResponse("Admin Creation Failed", false, HttpStatus.OK, null);
		}
	}

	@PostMapping("/createRecruiter")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> createRecruiter(@AuthenticationPrincipal  UserDetails userDetails,
			@Valid @RequestBody RecruiterRequest recruiter) {
		try {
			Admin admin = adminRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Admin not found"));
		
			if(recruiterRepository.findByEmail(recruiter.getEmail()).isPresent()) {
				throw new CustomExceptions.DuplicateResourceException("Recruiter with email " + recruiter.getEmail() + " already exists.");
			}

			Recruiter recruiterResponse = adminService.createRecruiter(recruiter, admin);
			return ResponseHandler.generateResponse("Recruiter Created", true, HttpStatus.OK, recruiterResponse);

		} catch (MailException e) {
			Logger.error("Failed to send activation email to {}: {}", recruiter.getEmail(), e.getMessage());
			throw e;
		} catch(CustomExceptions.DuplicateResourceException ex) {
			return ResponseHandler.generateResponse("Email Already Exists..!!", false, HttpStatus.OK,ex.getLocalizedMessage());
		}catch (Exception e) {
			Logger.error("Unexpected error occurred while creating recruiter: {}", e.getLocalizedMessage());
			return ResponseHandler.generateResponse("An error occurred while creating the recruiter", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/getAll")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getAllAdmins(
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable, @RequestParam(required = false) String searchKeyword) {
		try {
			Page<Admin> admin = adminRepository.findAllAdminsByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("All Admins Retrieved Successfully", true, HttpStatus.OK,admin);
		}catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.DuplicateResourceException e) {
			throw e;
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> update(@PathVariable Long id,@RequestBody Admin admin){
		try {
			Admin adminResponse = adminService.update(id,admin);
			if (adminResponse == null) {
				return ResponseHandler.generateResponse("No admin details found", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Admin Updated", true, HttpStatus.OK, adminResponse);
			}
		} catch (Exception ex) {
			Logger.error("Updating Admin details failed" + ex.getMessage());
			return ResponseHandler.generateResponse("Updating Admin details failed", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> findById(@PathVariable Long id){
		try {
			Optional<Admin> adminResponse = adminRepository.findById(id);
			if (adminResponse == null) {
				return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Id Founded Successfully", true, HttpStatus.OK, adminResponse);
			}
		} catch (Exception ex) {
			Logger.error("Id Not found" + ex.getMessage());
			return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteSoft(@PathVariable Long id){
		try {
			Optional<Admin> adminId = adminRepository.findById(id);
			if (adminId == null) {
				return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
			} else {
				adminService.deleteSoft(id);
				return ResponseHandler.generateResponse("Id Deleted Successfully", true, HttpStatus.OK,null);
			}
		} catch (Exception ex) {
			Logger.error("Id Not found" + ex.getMessage());
			return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
		}
	}
	
}
