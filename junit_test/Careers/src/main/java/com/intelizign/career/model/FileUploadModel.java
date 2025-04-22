package com.intelizign.career.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_upload_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "supporting_files_name")
    private String supporting_files_name;

    @Column(name = "supporting_files_url")
    private String supporting_files_url;

    @Column(name = "supporting_files_view_url")
    private String supporting_file_view_url;

    @Column(name = "mapped")
    private Boolean mapped;

    @Column(name = "upload_by")
    private String upload_by;

    @Column(name = "upload_on")
    private LocalDateTime upload_on;

//    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  
//    private Applicant applicant;
}

