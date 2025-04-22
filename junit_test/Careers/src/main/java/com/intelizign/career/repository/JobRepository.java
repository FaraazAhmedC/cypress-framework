package com.intelizign.career.repository;
 
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.intelizign.career.model.Job;
import com.intelizign.career.dto.JobDTO;
import com.intelizign.career.dto.KeySkillDTO;
import com.intelizign.career.dto.LocationDTO;
import jakarta.transaction.Transactional;
 
@Repository
@Transactional
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
 
	@Query("SELECT new com.intelizign.career.dto.JobDTO(j.id, j.jobTitle, j.jobDescription, j.jobRole, "
	        + "j.industryType, j.department, j.employmentType, j.roleCategory, j.jobExperience, "
	        + "j.education, j.shortDescription) "
	        + "FROM Job j "
	        + "WHERE "
	        + "(LOWER(CONCAT(j.department, j.industryType, j.jobTitle, j.jobDescription, j.jobRole, j.education, j.roleCategory)) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
	        + "OR :searchKeyword IS NULL OR :searchKeyword = '') "
	        + "AND j.active = true AND j.recruiterEmail = :recruiterMail ")
	Page<JobDTO> getJobsByRecruiter(String searchKeyword, String recruiterMail, Pageable pageable);
	
	@Query("SELECT new com.intelizign.career.dto.JobDTO(j.id, j.jobTitle, j.jobDescription, j.jobRole, "
	        + "j.industryType, j.department, j.employmentType, j.roleCategory, j.jobExperience, "
	        + "j.education, j.shortDescription) "
	        + "FROM Job j "
	        + "WHERE "
	        + "(LOWER(CONCAT(j.department, j.industryType, j.jobTitle, j.jobDescription, j.jobRole, j.education, j.roleCategory)) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
	        + "OR :searchKeyword IS NULL OR :searchKeyword = '') "
	        + "AND j.active = true")
	Page<JobDTO> getAllActiveJobs(String searchKeyword, Pageable pageable);
	
	@Query("SELECT new com.intelizign.career.dto.LocationDTO(l.id, l.location) FROM Job j JOIN j.location l WHERE j.id = :jobId")
	List<LocationDTO> findLocationsByJobId(@Param("jobId") Long jobId);
	
	@Query("SELECT new com.intelizign.career.dto.KeySkillDTO(k.id, k.skill) FROM Job j JOIN j.keySkills k WHERE j.id = :jobId")
	List<KeySkillDTO> findKeySkillsByJobId(@Param("jobId") Long jobId);
	
	@Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Job l WHERE LOWER(l.jobTitle) = LOWER(:jobTitle)")
	boolean existsByJobTitle(String jobTitle);
	
	@Query("SELECT j FROM Job j WHERE LOWER(j.jobTitle) = LOWER(:jobTitle)")
	Optional<Job> findByJobTitle(String jobTitle);
 
}