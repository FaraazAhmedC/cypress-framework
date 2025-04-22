package com.intelizign.career.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeySkill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String skill;

	@ManyToMany(mappedBy = "keySkills")
	@JsonBackReference  
	private List<Job> jobs;

	public KeySkill(Long id, String skill) {
		super();
		this.id = id;
		this.skill = skill;
	}
}
