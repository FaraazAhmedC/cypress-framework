package com.intelizign.career.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.Recruiter;

import jakarta.transaction.Transactional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
	
    Optional<Recruiter> findByEmail(String email);
	
//	Optional<Recruiter> findByOneTimeToken(String token);
	
	@Query("SELECT r FROM Recruiter r WHERE "
		       + "(LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR ?1 IS NULL OR ?1 = '') "
		       + "AND r.active = true")
	Page<Recruiter> findAllRecruitersByPagination(String searchKeyword, Pageable pageable);
}
