package com.intelizign.career.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.FileUploadModel;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadModel, Long>
{
	@Query("SELECT u FROM FileUploadModel u WHERE u.id = ?1")
	Optional<FileUploadModel> findById(Long id);

	@Modifying
	@Transactional
	void deleteAllByMapped(boolean b);
}
