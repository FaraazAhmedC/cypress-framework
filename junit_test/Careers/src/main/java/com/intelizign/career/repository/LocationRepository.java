package com.intelizign.career.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.career.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>{

	@Query("SELECT r FROM Location r WHERE "
			+ "(LOWER(CONCAT(r.location)) LIKE LOWER(CONCAT('%', ?1, '%')) "
			+ "OR ?1 IS NULL OR ?1 = '') ")
	Page<Location> findAllLocationsByPagination(String searchKeyword, Pageable pageable);

	Optional<Location> findByLocation(String location);

	@Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Location l WHERE l.location = :location")
	boolean existsByLocation(String location);

}
