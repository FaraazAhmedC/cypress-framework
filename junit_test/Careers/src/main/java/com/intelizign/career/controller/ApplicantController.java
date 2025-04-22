package com.intelizign.career.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intelizign.career.dto.ApplicantDTO;
import com.intelizign.career.dto.ApplicantStatusCountDTO;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Applicant;
import com.intelizign.career.repository.ApplicantRepository;
import com.intelizign.career.request.JobApplicationRequest;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.service.ApplicantService;

@RestController
@RequestMapping("/applicant")
public class ApplicantController {

	@Autowired
	private ApplicantService applicantService;
	@Autowired
	private ApplicantRepository applicantRepository;

	Logger Logger = LoggerFactory.getLogger(ApplicantController.class);

	@PostMapping("/{jobId}/apply")
	public ResponseEntity<Object> createApplicant(@PathVariable Long jobId, @ModelAttribute JobApplicationRequest request) {
		try {
			Applicant createdApplicant = applicantService.createApplicant(request, jobId);
			if (createdApplicant == null) {
				return ResponseHandler.generateResponse("Applicant Creation Failed", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Applicant Created", true, HttpStatus.OK, createdApplicant);
			}
		} catch(DataIntegrityViolationException ex) {
			Logger.error("Applicant is not created " + ex.getMessage());
			return ResponseHandler.generateResponse("Data is already present", false, HttpStatus.OK, null);
		} catch (Exception ex) {
			Logger.error("Applicant is not created " + ex.getMessage());
			return ResponseHandler.generateResponse("Applicant Creation Failed", false, HttpStatus.OK, null);
		}
	}

	@PutMapping("{id}/updateStatus/{status_id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> updateJobStatus(@PathVariable Long id, @PathVariable Integer status_id) {
		try {
			ApplicantDTO updatedJob = applicantService.updateJobStatus(id, status_id);
			return ResponseHandler.generateResponse("Status updated", true, HttpStatus.OK, updatedJob);
		} catch (Exception ex) {
			Logger.error("Applicant is not updated " + ex.getMessage());
			return ResponseHandler.generateResponse("Status could not be updated", false, HttpStatus.OK, null);
		}
	}

	@PutMapping("/update")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> updateApplicant(@RequestBody Applicant applicant, @PathVariable Long id) {
		try {
			Applicant applicant1 = applicantService.updateApplicant(applicant, id);
			if (applicant1 == null) {
				return ResponseHandler.generateResponse("Applicant Update Failed", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Applicant Updated", true, HttpStatus.OK, applicant1);
			}
		} catch (Exception ex) {
			Logger.error("Applicant is not created " + ex.getMessage());
			return ResponseHandler.generateResponse("Applicant Update Failed", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
		try {
			Applicant applicant = applicantRepository.findById(id).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Applicant Not Found With ID: " + id));
			ApplicantDTO dto = new ApplicantDTO(applicant.getId(), applicant.getFirstName(), applicant.getLastName(), applicant.getEmail(), applicant.getMobile(), applicant.getStatus(), applicant.getResume().getSupporting_files_url());
			return ResponseHandler.generateResponse("Applicant Retrived", true, HttpStatus.OK, dto);
		} catch (Exception ex) {
			Logger.error("Applicant is not retrived " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/getAll")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getAllAdmins(
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword) {
		try {
			Page<Applicant> applicants = applicantRepository.findAllApplicantsByPagination(searchKeyword, pageable);
			Page<ApplicantDTO> applicantDTO = applicants.map(ApplicantService::toDTO);
			return ResponseHandler.generateResponse("All Applicants Retrieved Successfully", true, HttpStatus.OK,
					applicantDTO);
		} catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.DuplicateResourceException e) {
			throw e;
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> deleteSoft(@PathVariable Long id) {
		try {
			Applicant applicant1 = applicantService.deleteSoft(id);
			if (applicant1 == null) {
				return ResponseHandler.generateResponse("Applicant Deletion Failed", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Applicant Deleted", true, HttpStatus.OK, null);
			}
		} catch (Exception ex) {
			Logger.error("Applicant is not deleted " + ex.getMessage());
			return ResponseHandler.generateResponse("Applicant deletion Failed", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/applicantStatistics")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getJobStatistics() {
		try {
			List<ApplicantStatusCountDTO> jobStats = applicantService.getApplicantStatistics();
			return ResponseHandler.generateResponse("Job statistics returned successfully", true, HttpStatus.OK,
					jobStats);
		} catch (Exception ex) {
			Logger.error("Error getting job statistics");
			return ResponseHandler.generateResponse("Error getting job statistics", false, HttpStatus.OK, null);
		}
	}
}
