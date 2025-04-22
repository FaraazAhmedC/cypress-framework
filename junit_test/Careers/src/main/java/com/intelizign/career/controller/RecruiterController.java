package com.intelizign.career.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.service.RecruiterService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {

	@Autowired
	private RecruiterService recruiterService;
	
	@Autowired
	private RecruiterRepository recruiterRepository;

    Logger logger = LoggerFactory.getLogger(RecruiterController.class);
     
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getAllRecruiters(
		@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable, @RequestParam(required = false) String searchKeyword) {
		try {
			Page<Recruiter> recruiter = recruiterRepository.findAllRecruitersByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("All Recruiters Retrieved Successfully", true, HttpStatus.OK, recruiter);
		}catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.DuplicateResourceException e) {
			logger.error("Error getting all recruiters: {}", e.getMessage());
			throw e;
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getRecruiterById(@PathVariable Long id){
		try {
			Optional<Recruiter> recruiter = recruiterRepository.findById(id);
			if(recruiter.isPresent()) {
				return ResponseHandler.generateResponse("Retrieved Recruiters By ID Successfully", true, HttpStatus.OK, recruiter.get());
			} else
				return ResponseHandler.generateResponse("Error getting recruiters", false, HttpStatus.OK, null);
		} catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.DuplicateResourceException e) {
			logger.error("Error getting recruiters: {}", e.getMessage());
			throw e;
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> updateRecruiter(@PathVariable Long id, @RequestBody Recruiter recruiter) {
		try {
			Recruiter recruiterResponse = recruiterService.updateRecruiter(id,recruiter);
			if (recruiterResponse == null) {
				return ResponseHandler.generateResponse("No Recruiter details found", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Recruiter Updated", true, HttpStatus.OK, recruiterResponse);
			}
		} catch (Exception ex) {
			logger.error("Updating Recruiter details failed" + ex.getMessage());
			return ResponseHandler.generateResponse("Updating Recruiter details failed", false, HttpStatus.OK, null);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteRecruiterSoft(@PathVariable Long id) {
		try {
			Optional<Recruiter> recruiterId = recruiterRepository.findById(id);
			if (recruiterId == null) {
				return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
			} else {
				recruiterService.deleteRecruiterSoft(id);
				return ResponseHandler.generateResponse("Id Deleted Successfully", true, HttpStatus.OK,null);
			}
		} catch (Exception ex) {
			logger.error("Id Not found" + ex.getMessage());
			return ResponseHandler.generateResponse("Id Not found", false, HttpStatus.OK, null);
		}
	}
	
	
	
	
}