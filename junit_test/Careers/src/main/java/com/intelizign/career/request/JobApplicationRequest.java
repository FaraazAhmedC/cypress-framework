package com.intelizign.career.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String mobileNo;
	private MultipartFile resume;
}
