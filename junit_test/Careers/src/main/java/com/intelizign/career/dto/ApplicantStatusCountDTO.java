package com.intelizign.career.dto;

public class ApplicantStatusCountDTO {
    private String status;
    private Long count;

    public ApplicantStatusCountDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public Long getCount() {
        return count;
    }
}
