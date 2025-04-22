package com.intelizign.career.dto;

import com.intelizign.career.model.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private JobStatus jobStatus;
    private String downloadResume;
}
