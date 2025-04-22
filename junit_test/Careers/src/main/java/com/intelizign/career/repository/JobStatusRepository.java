package com.intelizign.career.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.intelizign.career.model.JobStatus;

public interface JobStatusRepository extends JpaRepository<JobStatus, Integer>{

	@Query("SELECT u FROM JobStatus u WHERE u.job_status=?1")
	Optional<JobStatus> findByJobStatus(String status);

	@Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM JobStatus j WHERE j.job_status = :eJobStatus")
	boolean existsByJobStatus(String eJobStatus);
	
	@Query("SELECT j FROM JobStatus j WHERE "
	 	       + "(LOWER(CONCAT(j.job_status)) LIKE LOWER(CONCAT('%', ?1, '%')) "
	 	       + "OR ?1 IS NULL OR ?1 = '') ")
	Page<JobStatus> findAllJobStatusByPagination(String searchKeyword, Pageable pageable);
}
