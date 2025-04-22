package com.intelizign.career.request;

import java.util.List;

import com.intelizign.career.model.KeySkill;
import com.intelizign.career.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostJobRequest {

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
    private List<Location> location;
    private List<KeySkill> keySkills;
}
