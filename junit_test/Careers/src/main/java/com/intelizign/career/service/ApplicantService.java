package com.intelizign.career.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.intelizign.career.dto.ApplicantDTO;
import com.intelizign.career.dto.ApplicantStatusCountDTO;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Applicant;
import com.intelizign.career.model.EJobStatus;
import com.intelizign.career.model.FileUploadModel;
import com.intelizign.career.model.Job;
import com.intelizign.career.model.JobStatus;
import com.intelizign.career.repository.ApplicantRepository;
import com.intelizign.career.repository.FileUploadRepository;
import com.intelizign.career.repository.JobRepository;
import com.intelizign.career.repository.JobStatusRepository;
import com.intelizign.career.request.JobApplicationRequest;

@Service
public class ApplicantService {

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private FileUploadRepository fileUploadRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private FilesStorageServicePath storageServicepath;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private Environment env;

	public List<ApplicantStatusCountDTO> getApplicantStatistics() {
	    List<ApplicantStatusCountDTO> stats = new ArrayList<>();

	    stats.add(new ApplicantStatusCountDTO("totalJobs", applicantRepository.count()));
	    stats.add(new ApplicantStatusCountDTO("selectedJobs", countByEJobStatus(EJobStatus.SELECTED)));
	    stats.add(new ApplicantStatusCountDTO("rejectedJobs", countByEJobStatus(EJobStatus.REJECTED)));
	    stats.add(new ApplicantStatusCountDTO("approvedJobs", countByEJobStatus(EJobStatus.APPROVED)));
	    stats.add(new ApplicantStatusCountDTO("pendingJobs", countByEJobStatus(EJobStatus.PENDING)));
	    stats.add(new ApplicantStatusCountDTO("joiningConfirmed", countByEJobStatus(EJobStatus.JOINING_CONFIRMED)));
	    stats.add(new ApplicantStatusCountDTO("underReview", countByEJobStatus(EJobStatus.UNDER_REVIEW)));
	    stats.add(new ApplicantStatusCountDTO("interviewScheduled", countByEJobStatus(EJobStatus.INTERVIEW_SCHEDULED)));
	    stats.add(new ApplicantStatusCountDTO("interviewCompleted", countByEJobStatus(EJobStatus.INTERVIEW_COMPLETED)));
	    stats.add(new ApplicantStatusCountDTO("onHold", countByEJobStatus(EJobStatus.ON_HOLD)));
	    return stats;
	}


	public long countByEJobStatus(EJobStatus statusEnum) {
	    JobStatus status = jobStatusRepository.findByJobStatus(statusEnum.name())
	        .orElseThrow(() -> new RuntimeException("Job status not found: " + statusEnum.name()));
	    return applicantRepository.countByStatus(status);
	}

	public Applicant createApplicant(JobApplicationRequest jobApplicationRequest, Long jobId) {
		Applicant applicant = new Applicant();
		applicant.setFirstName(jobApplicationRequest.getFirstName());
		applicant.setLastName(jobApplicationRequest.getLastName());
		applicant.setEmail(jobApplicationRequest.getEmail());
		applicant.setMobile(jobApplicationRequest.getMobileNo());

		JobStatus pendingStatus = jobStatusRepository.findByJobStatus("PENDING")
				.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Check if Job Status is available"));
		applicant.setStatus(pendingStatus);

		if (applicant.getJobs() == null) {
			applicant.setJobs(new ArrayList<>());
		}

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Job not found with ID: " + jobId));
		if (applicant.getJobs().contains(job)) {
			throw new CustomExceptions.ResourceNotFoundException("Job is already mapped to this applicant");
		}
		applicant.getJobs().add(job);
		Applicant savedApplicant = applicantRepository.save(applicant);
		MultipartFile resumeFile = jobApplicationRequest.getResume();
		if (resumeFile != null && !resumeFile.isEmpty()) {
			FileUploadModel resume = new FileUploadModel();
			String hostname = env.getProperty("hostname.name");
			String filename = storageServicepath.save(resumeFile);
			resume.setSupporting_files_name(filename);
			resume.setSupporting_files_url(hostname + "/fileupload/attachments?filename=" + filename);
			resume.setSupporting_file_view_url(hostname + "/resume/viewfile/" + filename);
			resume.setMapped(true); // Mark as mapped to applicant
			resume.setUpload_by(jobApplicationRequest.getEmail());
			resume.setUpload_on(LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))));

			// Save resume and associate with applicant
			FileUploadModel savedResume = fileUploadRepository.save(resume);
			savedApplicant.setResume(savedResume);
			applicantRepository.save(savedApplicant);
		}
		return savedApplicant;
	}

	public Applicant updateApplicant(Applicant applicant, Long id) {
		Applicant applicant2 = applicantRepository.findById(id).get();
		if (applicant2 == null) {
			return null;
		}
		applicant2.setFirstName(applicant.getFirstName());
		applicant2.setLastName(applicant.getLastName());
		applicant2.setEmail(applicant.getEmail());
		applicant2.setMobile(applicant.getMobile());
		applicant2.setStatus(applicant.getStatus());
		return applicantRepository.save(applicant2);
	}

	public List<Applicant> findAll() {
		return applicantRepository.findAll();
	}

	public Applicant deleteSoft(Long id) {
		Applicant applicant = applicantRepository.findById(id).orElseThrow(
				() -> new CustomExceptions.ResourceNotFoundException("Applicant Not Found With ID: " + id));
		applicant.setActive(false);
		return applicantRepository.save(applicant);
	}

	public String deleteHard(Long id) {
		Applicant applicant2 = applicantRepository.findById(id).get();
		if (applicant2 == null) {
			return "Id Not exists";
		}
		applicantRepository.deleteById(id);
		return "Id deleted Successfully " + applicant2;
	}

	public ApplicantDTO updateJobStatus(Long id, Integer statusId) {
		Applicant found_applicant = applicantRepository.findById(id).orElseThrow(
				() -> new CustomExceptions.ResourceNotFoundException("Applicant Not Found With ID: " + id));
		JobStatus jobStatus = jobStatusRepository.findById(statusId).orElseThrow(
				() -> new CustomExceptions.ResourceNotFoundException("Job Status not Found With ID: " + id));
		found_applicant.setStatus(jobStatus);
		Applicant applicant = applicantRepository.save(found_applicant);
		return new ApplicantDTO(applicant.getId(), applicant.getFirstName(), applicant.getLastName(),
				applicant.getEmail(), applicant.getMobile(), applicant.getStatus(),
				applicant.getResume().getSupporting_file_view_url());
	}
	
	public static ApplicantDTO toDTO(Applicant applicant) {
        // map only needed fields
        ApplicantDTO dto = new ApplicantDTO();
        dto.setId(applicant.getId());
        dto.setFirstName(applicant.getFirstName());
        dto.setLastName(applicant.getLastName());
        dto.setEmail(applicant.getEmail());
        dto.setMobile(applicant.getMobile());
        dto.setJobStatus(applicant.getStatus());
        dto.setDownloadResume(applicant.getResume().getSupporting_files_url());
        return dto;
    }
}
