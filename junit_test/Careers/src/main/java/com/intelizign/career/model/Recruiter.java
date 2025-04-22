package com.intelizign.career.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Recruiter first name cannot be blank")
	private String firstName;

	@NotBlank(message = "Recruiter last name cannot be blank")
	private String lastName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Column(unique = true)
	private String email;

	private String name;

	private String mobile;

	private String location;

	@NotBlank(message = "Password cannot be blank")
	@Size(min = 12, message = "Password must be at least 6 characters long")
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role = Role.RECRUITER;

	@Column(name = "active")
	private Boolean active = true;

	@ManyToOne
	@JoinColumn(name = "admin_id", nullable = false)
	@JsonBackReference
	private Admin admin;

	@JsonIgnore
	@OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
	private List<Job> jobs;

	@PrePersist
	@PreUpdate
	private void generateName() {
		this.name = this.firstName +" "+this.lastName;
	}

	public Recruiter(@NotBlank(message = "Recruiter first name cannot be blank") String firstName,
			@NotBlank(message = "Recruiter last name cannot be blank") String lastName,
			@NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email format") String email,
			String mobile, String location,
			@NotBlank(message = "Password cannot be blank") @Size(min = 6, message = "Password must be at least 6 characters long") String password,
			Admin admin) {
		super();
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.location = location;
		this.password = password;
		this.admin = admin;
	}	
}
