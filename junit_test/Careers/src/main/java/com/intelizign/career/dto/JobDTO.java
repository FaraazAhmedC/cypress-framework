package com.intelizign.career.dto;
 
import java.util.ArrayList;
import java.util.List;
 
import com.intelizign.career.model.KeySkill;
import com.intelizign.career.model.Location;
 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;
    private String jobTitle;
    private String jobDescription;
    private String jobRole;
    private String industryType;
    private String department;
    private String employmentType;
    private String roleCategory;
    private String jobExperience;
    private String education;
    private String shortDescription;
    private List<LocationDTO> locations;
    private List<KeySkillDTO> keySkills;
    public JobDTO(Long id, String jobTitle, String jobDescription, String jobRole, String industryType,
			String department, String employmentType, String roleCategory, String jobExperience, String education,
			String shortDescription) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.jobDescription = jobDescription;
		this.jobRole = jobRole;
		this.industryType = industryType;
		this.department = department;
		this.employmentType = employmentType;
		this.roleCategory = roleCategory;
		this.jobExperience = jobExperience;
		this.education = education;
		this.shortDescription = shortDescription;
	}
 
	public JobDTO(Long id, String jobTitle, String jobDescription, String jobRole, String industryType,
			String department, String employmentType, String roleCategory, String jobExperience, String education,
			String shortDescription, LocationDTO location, KeySkillDTO keySkill) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.jobDescription = jobDescription;
		this.jobRole = jobRole;
		this.industryType = industryType;
		this.department = department;
		this.employmentType = employmentType;
		this.roleCategory = roleCategory;
		this.jobExperience = jobExperience;
		this.education = education;
		this.shortDescription = shortDescription;
		if (locations != null) {
	        this.locations = new ArrayList<>();
	        this.locations.add(location);
	    }
	    if (keySkills != null) {
	        this.keySkills = new ArrayList<>();
	        this.keySkills.add(keySkill);
	    }
	}  
}