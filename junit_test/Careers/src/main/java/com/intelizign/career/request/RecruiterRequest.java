package com.intelizign.career.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruiterRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private String location;
	private String password;
	private String confirmPassword;
	
	public RecruiterRequest(String firstName, String lastName, String email, String mobile, String location,
			String password, String confirmPassword) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.location = location;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}		
}
