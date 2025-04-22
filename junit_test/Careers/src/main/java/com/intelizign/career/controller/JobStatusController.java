package com.intelizign.career.controller;
 
import java.util.List;
import java.util.Optional;

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
import com.intelizign.career.model.EJobStatus;
import com.intelizign.career.model.JobStatus;
import com.intelizign.career.repository.JobStatusRepository;
import com.intelizign.career.request.JobStatusRequest;
import com.intelizign.career.response.ResponseHandler;
 
@RestController
@RequestMapping("/jobStatus")
public class JobStatusController {
 
	@Autowired
	private JobStatusRepository jobStatusRepository;
 
	Logger Logger = LoggerFactory.getLogger(JobStatusController.class);
 
	// Default insert of Job Status for inserting Predefined Jobs
	@PostMapping("/insertJobStatus")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> insertAll() {
		try {
			for (EJobStatus eJobStatus : EJobStatus.values()) {
				if (!jobStatusRepository.existsByJobStatus(eJobStatus.toString())) { // Avoid duplicates
					JobStatus jobStatus = new JobStatus();
					jobStatus.setJob_status(eJobStatus.toString());
					jobStatusRepository.save(jobStatus);
				}
			}
			return ResponseHandler.generateResponse("Job Status Added Successfully", true, HttpStatus.OK, null);
		} catch (Exception e) {
			Logger.error("Internal Server Error while insertrole:{}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}
 
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PostMapping("/createJobStatus")
	public ResponseEntity<Object> createJobStatus(@RequestBody JobStatusRequest jobStatusRequest) {
		try {
			Optional<JobStatus> existingJobStatus = jobStatusRepository
					.findByJobStatus(jobStatusRequest.getJobStatusName().toUpperCase());
			if (existingJobStatus.isPresent()) {
				return ResponseHandler.generateResponse("Check if already jobStatus exists", false, HttpStatus.OK,
						null);
			} else {
				JobStatus jobStatus = new JobStatus();
				jobStatus.setJob_status(jobStatusRequest.getJobStatusName().toUpperCase());
				JobStatus addedJobStatus = jobStatusRepository.save(jobStatus);
				return ResponseHandler.generateResponse("jobStatus added", true, HttpStatus.OK, addedJobStatus);
			}
		} catch (Exception ex) {
			Logger.error("Error creating job Status: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error creating job Status", false, HttpStatus.OK, null);
		}
	}
 
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateSkill(@PathVariable Integer id,
			@RequestBody JobStatusRequest jobStatusRequest) {
		try {
			Optional<JobStatus> jobStatus = jobStatusRepository.findById(id);
			if(jobStatusRepository
					.findByJobStatus(jobStatusRequest.getJobStatusName().toUpperCase()).isPresent()) {
				return ResponseHandler.generateResponse("JobStatus already exists", false, HttpStatus.OK, null);
			}
			if (jobStatus.isPresent()) {
				jobStatus.get().setJob_status(jobStatusRequest.getJobStatusName().toUpperCase());
				JobStatus updatedJobStatus = jobStatusRepository.save(jobStatus.get());
				return ResponseHandler.generateResponse("JobStatus updated", true, HttpStatus.OK, updatedJobStatus);
 
			} else {
				return ResponseHandler.generateResponse("JobStatus does not exists", false, HttpStatus.OK, null);
			}
		} catch (Exception ex) {
			Logger.error("Error creating JobStatus: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error updating JobStatus", false, HttpStatus.OK, null);
		}
	}
 
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getJobStatusByID(@PathVariable Integer id) {
		try {
			JobStatus jobStatus = jobStatusRepository.findById(id).orElseThrow(
					() -> new CustomExceptions.ResourceNotFoundException("Job Status Not Found With ID: " + id));
			return ResponseHandler.generateResponse("Job Status Retrived", true, HttpStatus.OK, jobStatus);
		} catch (Exception ex) {
			Logger.error("Job Status is not retrived " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}
 
	@GetMapping("/getAllJobStatus")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getAllJobStatus() {
		try {
			List<JobStatus> jobStatus = jobStatusRepository.findAll();
			return ResponseHandler.generateResponse("Job Status Retrived", true, HttpStatus.OK, jobStatus);
		} catch (Exception ex) {
			Logger.error("Applicant is not retrived " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@GetMapping("/getAll")
	public ResponseEntity<Object> getAllJobStatusByPagination(
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable, @RequestParam(required = false) String searchKeyword) {
		try {
			Page<JobStatus> jobStatuses = jobStatusRepository.findAllJobStatusByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("All Job Status Retrieved Successfully", true, HttpStatus.OK,jobStatuses);
		}catch (Exception ex) {
			return ResponseHandler.generateResponse("Job Status could not be retrieved", false, HttpStatus.OK, null);
		}
	}
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> deleteJobStatus(@PathVariable Integer id) {
	    try {
	        Optional<JobStatus> jobStatusOptional = jobStatusRepository.findById(id);
	        if (jobStatusOptional.isPresent()) {
	            jobStatusRepository.deleteById(id);
	            return ResponseHandler.generateResponse("Job Status deleted successfully", true, HttpStatus.OK, null);
	        } else {
	            return ResponseHandler.generateResponse("Job Status not found", false, HttpStatus.OK, null);
	        }
	    } catch (DataIntegrityViolationException e) {
			throw new CustomExceptions.DuplicateResourceException("Cannot delete Job Status with ID " + id + " due to database constraints");
		} catch (Exception ex) {
	        Logger.error("Error deleting Job Status: " + ex.getMessage());
	        return ResponseHandler.generateResponse("Error deleting Job Status", false, HttpStatus.OK, null);
	    }
	}
 
}