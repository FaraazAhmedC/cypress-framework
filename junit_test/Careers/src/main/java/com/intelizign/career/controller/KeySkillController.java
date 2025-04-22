package com.intelizign.career.controller;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.intelizign.career.model.KeySkill;
import com.intelizign.career.repository.KeySkillRepository;
import com.intelizign.career.request.SkillRequest;
import com.intelizign.career.response.ResponseHandler;

@RestController
@RequestMapping("/keySkill")
public class KeySkillController {

	@Autowired
	private KeySkillRepository skillRepository;
	
	Logger logger = LoggerFactory.getLogger(KeySkillController.class);
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PostMapping("/insertSkills")
	public ResponseEntity<Object> createSkills(@RequestBody List<KeySkill> skillrequest) {
		try {
			if (!skillrequest.isEmpty()) {
				for (KeySkill skill : skillrequest) {
					if (!skillRepository.existsBySkill(skill.getSkill())) {
						KeySkill skillObj = new KeySkill();
						skillObj.setSkill(skill.getSkill());
						skillRepository.save(skillObj);
					}
				}
				return ResponseHandler.generateResponse("Skills Created Successfully", true, HttpStatus.OK,null);
			} else {
				return ResponseHandler.generateResponse("Skills are empty", false, HttpStatus.OK, null);
			}
 
		} catch (Exception ex) {
			logger.error("Error creating skill: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error creating skills", false, HttpStatus.OK, null);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PostMapping("/createSkill")
	public ResponseEntity<Object> createSkill(@RequestBody SkillRequest skillRequest){
		try {
			Optional<KeySkill> existingSkill = skillRepository.findBySkill(skillRequest.getSkillName());
			if(existingSkill.isPresent()) {
				return ResponseHandler.generateResponse("Check if already skill exists", false, HttpStatus.OK, null);
			} else {
				KeySkill skill = new KeySkill();
				skill.setSkill(skillRequest.getSkillName());
				KeySkill addedSkill = skillRepository.save(skill);
				return ResponseHandler.generateResponse("Skill added", true, HttpStatus.OK, addedSkill);
			}
		} catch(Exception ex) {
			logger.error("Error creating skill: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error creating skill", false, HttpStatus.OK, null);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateSkill(@PathVariable Long id, @RequestBody SkillRequest skillRequest){
		try {
			Optional<KeySkill> skill = skillRepository.findById(id);
			if(skill.isPresent()) {
				skill.get().setSkill(skillRequest.getSkillName());
				KeySkill updatedSkill = skillRepository.save(skill.get());
				return ResponseHandler.generateResponse("Skill updated", true, HttpStatus.OK, updatedSkill);
				
			} else {
				return ResponseHandler.generateResponse("Skill does not exists", false, HttpStatus.OK, null);			
			}
		} catch(Exception ex) {
			logger.error("Error creating skill: {}", ex.getMessage());
			return ResponseHandler.generateResponse("Error updating skill", false, HttpStatus.OK, null);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@GetMapping("/getAll")
	public ResponseEntity<Object> getAllAdmins(
			@PageableDefault(size = 10, page = 1, sort = "id", direction = Direction.DESC) Pageable pageable, @RequestParam(required = false) String searchKeyword) {
		try {
			Page<KeySkill> skills = skillRepository.findAllSkillsByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("All KeySkills Retrieved Successfully", true, HttpStatus.OK,skills);
		}catch (Exception ex) {
			return ResponseHandler.generateResponse("Skill could not be retrieved", false, HttpStatus.OK, null);
		}
	}
	
	
	
	@PreAuthorize("hasAnyAuthority('RECRUITER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteSkill(@PathVariable Long id){
		try {
			 skillRepository.deleteById(id);
			return ResponseHandler.generateResponse("Skill Deleted", true, HttpStatus.OK,null);
		} catch (DataIntegrityViolationException e) {
			throw new CustomExceptions.DuplicateResourceException("Cannot delete Skill with ID " + id + " due to database constraints");
		} catch(Exception ex) {
			return ResponseHandler.generateResponse("Skill not Deleted", false, HttpStatus.OK, null);
		}
	}
}
