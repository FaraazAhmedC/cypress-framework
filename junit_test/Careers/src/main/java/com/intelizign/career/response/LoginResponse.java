package com.intelizign.career.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
	private Long id;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
    private String role;
    private String token;

}