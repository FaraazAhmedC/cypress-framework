package com.intelizign.career.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intelizign.career.dto.JobDTO;
import com.intelizign.career.dto.KeySkillDTO;
import com.intelizign.career.dto.LocationDTO;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.exception.CustomExceptions.ResourceNotFoundException;
import com.intelizign.career.model.Job;
import com.intelizign.career.model.Location;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.JobRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.request.PostJobRequest;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/job")
public class JobController {

	@Autowired
	private JobService jobService;

	@Autowired
	private RecruiterRepository recruiterRepository;

	@Autowired
	private JobRepository jobRepository;

	Logger logger = LoggerFactory.getLogger(JobController.class);

	@PreAuthorize("hasAuthority('RECRUITER')")
	@PostMapping("/create")
	public ResponseEntity<Object> createJob(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody PostJobRequest jobRequest) {
 
		try {
			Recruiter recruiter = recruiterRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recruiter not found"));
			
			for (Location location : jobRequest.getLocation()) {
				if (jobRepository.existsByJobTitle(jobRequest.getJobTitle())) {
					Job foundJob = jobRepository.findByJobTitle(jobRequest.getJobTitle()).get();
					if (foundJob.getLocation().stream()
							.anyMatch(existingLocation -> existingLocation.getId().equals(location.getId()))) {
						// Job with the same title and location exists, throw exception
						return ResponseHandler.generateResponse("Job already present in the location", false, HttpStatus.OK, null);
					}
				}
			}
 
			Job job = new Job(jobRequest.getJobTitle(), jobRequest.getJobDescription(), jobRequest.getJobRole(),
					jobRequest.getIndustryType(), jobRequest.getDepartment(), jobRequest.getEmploymentType(),
					jobRequest.getRoleCategory(), jobRequest.getJobExperience(), jobRequest.getEducation(),
					jobRequest.getShortDescription());
			job.setJobPostedBy(recruiter.getFirstName());
			job.setRecruiterEmail(recruiter.getEmail());
			job.setRecruiterMobile(recruiter.getMobile());
			job.setRecruiter(recruiter);
			job.setKeySkills(jobRequest.getKeySkills());
			job.setLocation(jobRequest.getLocation());
			Job createdJob = jobService.createJob(job);
			logger.info("Job Data: " + createdJob.toString());
			return ResponseHandler.generateResponse("Job Created Successfully", true, HttpStatus.OK, createdJob);
 
		} catch (CustomExceptions.ResourceNotFoundException e) {
			logger.error("Error creating job: {}", e.getMessage());
			throw e;
		} catch (Exception ex) {
			return ResponseHandler.generateResponse("Error getting while job creation", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/{jobId}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getJobById(@PathVariable Long jobId) {
		try {
			Job job = jobRepository.findById(jobId).get();
			return ResponseHandler.generateResponse("Successfully Retrieved jobs by ID.", true, HttpStatus.OK, job);
		} catch (Exception ex) {
			logger.error("Error getting retrieved jobs by id: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error getting retrieved jobs by id.", false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/recruiter/getAll")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getJobsByRecruiter(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false) String searchKeyword,
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable) {
		try {
			Recruiter recruiter = recruiterRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recruiter not found"));

			Page<JobDTO> jobs = jobRepository.getJobsByRecruiter(searchKeyword, recruiter.getEmail(), pageable);

			jobs.getContent().forEach(job -> {
				List<LocationDTO> locations = jobRepository.findLocationsByJobId(job.getId());
				List<KeySkillDTO> keySkills = jobRepository.findKeySkillsByJobId(job.getId());
				job.setLocations(locations);
				job.setKeySkills(keySkills);
			});

			return ResponseHandler.generateResponse("Retrieved Jobs By RecruiterId Successfully", true, HttpStatus.OK,
					jobs);

		} catch (Exception ex) {
			logger.error("Error getting retrieved jobs by recruiterid: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error getting retrieved jobs by recruiterid.", false,
					HttpStatus.OK, null);
		}
	}

	@GetMapping("/getAll")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getJobsByRecruiter(@RequestParam(required = false) String searchKeyword,
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable) {
		try {

			Page<JobDTO> jobs = jobRepository.getAllActiveJobs(searchKeyword, pageable);

			jobs.getContent().forEach(job -> {
				List<LocationDTO> locations = jobRepository.findLocationsByJobId(job.getId());
				List<KeySkillDTO> keySkills = jobRepository.findKeySkillsByJobId(job.getId());
				job.setLocations(locations);
				job.setKeySkills(keySkills);
			});

			return ResponseHandler.generateResponse("Retrieved Jobs Successfully", true, HttpStatus.OK, jobs);
		} catch (Exception ex) {
			logger.error("Error getting retrieved jobs by recruiterid: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error getting retrieved jobs by recruiterid.", false,
					HttpStatus.OK, null);
		}
	}

	@PutMapping("/{jobId}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> updateJob(@PathVariable Long jobId, @RequestBody PostJobRequest job) {
		try {
			Job updatedJob = jobService.updateJob(jobId, job);
			return ResponseHandler.generateResponse("Updated job successfully", true, HttpStatus.OK, updatedJob);
		} catch (Exception ex) {
			logger.error("Job updation failed", ex.getMessage());
			return ResponseHandler.generateResponse("Job updation failed", false, HttpStatus.OK, null);
		}
	}

	@DeleteMapping("/{jobId}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> deleteJob(@PathVariable Long jobId) {
		try {
			if (jobId == null) {
				return ResponseHandler.generateResponse("Job ID must be provided", false, HttpStatus.OK, null);
			}
			Job job = jobRepository.findById(jobId)
					.orElseThrow(() -> new ResourceNotFoundException("Job with ID " + jobId + " not found"));
			jobService.deleteJob(job);
			return ResponseHandler.generateResponse("Job deleted successfully", true, HttpStatus.OK, null);
		} catch (DataIntegrityViolationException e) {
			throw new CustomExceptions.DuplicateResourceException("Cannot delete job with ID " + jobId + " due to database constraints");
		} catch (Exception ex) {
			return ResponseHandler.generateResponse("An unexpected error occurred while deleting job with ID", false,
					HttpStatus.OK, null);
		}
	}
}
