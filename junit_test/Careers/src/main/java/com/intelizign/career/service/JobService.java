package com.intelizign.career.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.exception.CustomExceptions.BadRequestException;
import com.intelizign.career.model.Job;
import com.intelizign.career.repository.JobRepository;
import com.intelizign.career.request.PostJobRequest;

@Service
public class JobService {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmailService emailService;

	public Job createJob(Job job) {

		if (job.getJobTitle() == null || job.getJobTitle().trim().isEmpty()) {
			throw new BadRequestException("Job title cannot be empty");
		}

		if (job.getJobDescription() == null || job.getJobDescription().trim().isEmpty()) {
			throw new BadRequestException("Job description cannot be empty");
		}
		return jobRepository.save(job);
	}

	public Job updateJob(Long jobId, PostJobRequest updatedJob) {
		return jobRepository.findById(jobId).map(existingJob -> {
			if (updatedJob.getJobTitle() != null)
				existingJob.setJobTitle(updatedJob.getJobTitle());
			if (updatedJob.getJobDescription() != null)
				existingJob.setJobDescription(updatedJob.getJobDescription());
			if (updatedJob.getJobRole() != null)
				existingJob.setJobRole(updatedJob.getJobRole());
			if (updatedJob.getIndustryType() != null)
				existingJob.setIndustryType(updatedJob.getIndustryType());
			if (updatedJob.getDepartment() != null)
				existingJob.setDepartment(updatedJob.getDepartment());
			if (updatedJob.getEmploymentType() != null)
				existingJob.setEmploymentType(updatedJob.getEmploymentType());
			if (updatedJob.getRoleCategory() != null)
				existingJob.setRoleCategory(updatedJob.getRoleCategory());
			if (updatedJob.getJobExperience() != null)
				existingJob.setJobExperience(updatedJob.getJobExperience());
			if (updatedJob.getEducation() != null)
				existingJob.setEducation(updatedJob.getEducation());
			if (updatedJob.getShortDescription() != null)
				existingJob.setShortDescription(updatedJob.getShortDescription());
			if (updatedJob.getLocation() != null)
				existingJob.setLocation(updatedJob.getLocation());

			if (updatedJob.getKeySkills() != null) {
				existingJob.setKeySkills(updatedJob.getKeySkills());
			}

			return jobRepository.save(existingJob);
		}).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Job not found"));
	}

	public void deleteJob(Job job) {
		job.setActive(false);
		job.getKeySkills().clear();
		job.getLocation().clear();
		jobRepository.save(job);
	}
}