package com.intelizign.career.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.Applicant;
import com.intelizign.career.model.JobStatus;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long>{
	
    long countByStatus(JobStatus status);
    
    @Query("SELECT r FROM Applicant r WHERE "
 	       + "(LOWER(CONCAT(r.firstName, r.lastName, r.email)) LIKE LOWER(CONCAT('%', ?1, '%')) "
 	       + "OR ?1 IS NULL OR ?1 = '') "
 	       + "AND r.active = true")
	Page<Applicant> findAllApplicantsByPagination(String searchKeyword, Pageable pageable);

}
