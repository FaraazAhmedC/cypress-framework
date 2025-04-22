package com.intelizign.career.model;

import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class) // Important for automatic timestamps
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String jobTitle;

	@Column(columnDefinition = "TEXT")
	private String jobDescription;

	private String jobRole;

	private String industryType;

	private String department;

	private String employmentType;

	private String roleCategory;

	private String jobExperience;

	private String education;

	@Column(columnDefinition = "TEXT")
	private String shortDescription;

	@ManyToMany
	@JoinTable(name = "job_location",
			joinColumns = @JoinColumn(name = "job_id"),
			inverseJoinColumns = @JoinColumn(name = "location_id") // Column for KeySkill
	)
	private List<Location> location;

	@ManyToMany
	@JoinTable(name = "job_key_skill", // Name of the join table
			joinColumns = @JoinColumn(name = "job_id"), // Column for Job
			inverseJoinColumns = @JoinColumn(name = "key_skill_id") // Column for KeySkill
	)
	private List<KeySkill> keySkills;

	private String jobPostedBy;

	private String recruiterEmail;

	private String recruiterMobile;

	@Column(name = "active")
	private Boolean active = true;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "recruiter_id")
	@JsonIgnore
	private Recruiter recruiter;

	@ManyToMany(mappedBy = "jobs")
	@JsonIgnore
	private List<Applicant> applicants;

	public void removeAllKeySkills() {
		this.keySkills.clear();
	}
	
	public void removeAllLocation() {
		this.location.clear();
	}

	public Job(String jobTitle, String jobDescription, String jobRole, String industryType, String department,
			String employmentType, String roleCategory, String jobExperience, String education, String shortDescription) {
		super();
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

	@Override
	public String toString() {
		return "Job [id=" + id + ", jobTitle=" + jobTitle + ", jobDescription=" + jobDescription + ", jobRole="
				+ jobRole + ", industryType=" + industryType + ", department=" + department + ", employmentType="
				+ employmentType + ", roleCategory=" + roleCategory + ", jobExperience=" + jobExperience
				+ ", education=" + education + ", shortDescription=" + shortDescription + ", location=" + location
				+ ", keySkills=" + keySkills + ", jobPostedBy=" + jobPostedBy + ", recruiterEmail=" + recruiterEmail
				+ ", recruiterMobile=" + recruiterMobile + ", active=" + active + ", recruiter=" + recruiter
				+ ", applicants=" + applicants + "]";
	}

}