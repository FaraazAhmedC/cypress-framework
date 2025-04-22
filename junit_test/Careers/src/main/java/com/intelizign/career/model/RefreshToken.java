package com.intelizign.career.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "refresh_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "id")
	private Admin admin;
	
	@OneToOne
	@JoinColumn(name = "recruiter_id", referencedColumnName = "id")
	private Recruiter recruiter;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Instant expiryDate;

}
