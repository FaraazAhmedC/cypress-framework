package com.intelizign.career.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applicant")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    private String mobile;
       
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private JobStatus status;
    
    @Column(name = "active")
	private Boolean active = true;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private FileUploadModel resume;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "applicant_jobs",    // The join table name
        joinColumns = @JoinColumn(name = "applicant_id"), // Foreign key column for Applicant
        inverseJoinColumns = @JoinColumn(name = "job_id") // Foreign key column for Job
    )
    @JsonManagedReference
    private List<Job> jobs;    
}

