package com.intelizign.career.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Location;
import com.intelizign.career.repository.LocationRepository;
import com.intelizign.career.response.ResponseHandler;
import com.intelizign.career.service.LocationService;

@RestController
@RequestMapping("/location")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@Autowired
	private LocationRepository locationRepository;

	Logger Logger = LoggerFactory.getLogger(ApplicantController.class);

	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PostMapping("/insertLocations")
	public ResponseEntity<Object> createLocations(@RequestBody List<Location> locationRequest) {
		try {
			if (!locationRequest.isEmpty()) {
				for (Location location : locationRequest) {
					if (!locationRepository.existsByLocation(location.getLocation())) {
						Location locationObj = new Location();
						locationObj.setLocation(location.getLocation());
						locationRepository.save(locationObj);
					}
				}
				return ResponseHandler.generateResponse("Locations Created Successfully", true, HttpStatus.OK,null);
			} else {
				return ResponseHandler.generateResponse("Locations are empty", false, HttpStatus.OK, null);
			}
 
		} catch (Exception ex) {
			Logger.error("Error creating Location: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error creating Locations", false, HttpStatus.OK, null);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<Object> createLocation(@RequestBody Location location) {
		try {
			Location createdLocation = locationService.createLocation(location);
			return ResponseHandler.generateResponse("Location Created", true, HttpStatus.OK, createdLocation);
		} catch (Exception ex) {
			Logger.error("Location is not created " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> updateLocation(@RequestBody Location location, @PathVariable Long id) {
		try {
			Location updatedLocation = locationService.updateLocation(location, id);
			return ResponseHandler.generateResponse("Location Updated", true, HttpStatus.OK, updatedLocation);
		} catch (Exception ex) {
			Logger.error("Location is not created " + ex.getMessage());
			return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.OK, null);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
		try {
			Location location = locationRepository.findById(id).orElseThrow(
					() -> new CustomExceptions.ResourceNotFoundException("Location Not Found With ID: " + id));
			return ResponseHandler.generateResponse("Retriving Location data successfully", true, HttpStatus.OK, location);
		} catch (Exception ex) {
			Logger.error("Location is not retrived " + ex.getMessage());
			return ResponseHandler.generateResponse("Location is not retrived", false, HttpStatus.OK, null);
		}

	}

	@GetMapping("/getAll")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> getAllLocations(
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword) {
		try {
			Page<Location> applicant = locationRepository.findAllLocationsByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("All Location Retrieved Successfully", true, HttpStatus.OK,
					applicant);
		} catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.DuplicateResourceException e) {
			throw e;
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	public ResponseEntity<Object> deleteHard(@PathVariable Long id) {
		try {
			String deleteLocation = locationService.deleteHard(id);
			if (deleteLocation == null) {
				return ResponseHandler.generateResponse("Location Deletion Failed", false, HttpStatus.OK, null);
			} else {
				return ResponseHandler.generateResponse("Location Deleted", true, HttpStatus.OK, null);
			}
		} catch (Exception ex) {
			Logger.error("Location is not deleted " + ex.getMessage());
			return ResponseHandler.generateResponse("Location deletion Failed", false, HttpStatus.OK, null);
		}

	}

}
