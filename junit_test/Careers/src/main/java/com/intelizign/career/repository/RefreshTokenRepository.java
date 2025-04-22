package com.intelizign.career.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String token);

	void deleteByToken(String token);

	@Transactional
	@Modifying
	int deleteByRecruiter(Recruiter recruiter);
	
	@Transactional
	@Modifying
	int deleteByAdmin(Admin admin);
	
	@Query("SELECT COUNT(r) > 0 FROM refresh_token r WHERE r.admin.id = :id OR r.recruiter.id = :id")
	boolean existsByUserID(Long id);
	
}
