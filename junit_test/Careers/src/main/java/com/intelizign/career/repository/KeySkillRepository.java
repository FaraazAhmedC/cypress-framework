package com.intelizign.career.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.intelizign.career.model.KeySkill;

public interface KeySkillRepository  extends JpaRepository<KeySkill, Long>{
	
	@Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KeySkill k WHERE k.skill = :skill")
	boolean existsBySkill(String skill);
	
	@Query("SELECT s FROM KeySkill s WHERE "
 	       + "(LOWER(CONCAT(s.skill)) LIKE LOWER(CONCAT('%', ?1, '%')) "
 	       + "OR ?1 IS NULL OR ?1 = '') ")
	Page<KeySkill> findAllSkillsByPagination(String searchKeyword, Pageable pageable);
	
	Optional<KeySkill> findBySkill(String skill);
}
