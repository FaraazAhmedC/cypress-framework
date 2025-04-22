package com.intelizign.career.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intelizign.career.exception.CustomExceptions;
import com.intelizign.career.model.Location;
import com.intelizign.career.repository.LocationRepository;

@Service
public class LocationService {
	@Autowired
	private LocationRepository locationRepository;

	public Location createLocation(Location location) {
		if(location.getLocation() == locationRepository.findByLocation(location.getLocation()).get().getLocation()) {
			throw new CustomExceptions.ResourceNotFoundException("Check if already location exists");
		}
		return locationRepository.save(location);
	}

	public Location updateLocation(Location location, Long id) {
		Location location2 = locationRepository.findById(id)
				.orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Location Not Found With ID: " + id));
		location2.setLocation(location.getLocation());;
		return locationRepository.save(location2);
	}

	public String deleteHard(Long id) {
		Location location = locationRepository.findById(id).get();
		if (location == null) {
			return "Id Not exists";
		}
		locationRepository.deleteById(id);
		return "Id deleted Successfully " + location;
	}
}
