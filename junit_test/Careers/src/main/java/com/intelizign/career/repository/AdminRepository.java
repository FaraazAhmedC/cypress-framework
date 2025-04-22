package com.intelizign.career.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;

import jakarta.transaction.Transactional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {	
	
    Optional<Admin> findByEmail(String email);
    
    @Query("SELECT r FROM Admin r WHERE "
    	       + "(LOWER(CONCAT(r.firstName, r.lastName, r.email, r.name)) LIKE LOWER(CONCAT('%', ?1, '%')) "
    	       + "OR ?1 IS NULL OR ?1 = '') "
    	       + "AND r.active = true")
    Page<Admin> findAllAdminsByPagination(String searchKeyword, Pageable pageable);

}
