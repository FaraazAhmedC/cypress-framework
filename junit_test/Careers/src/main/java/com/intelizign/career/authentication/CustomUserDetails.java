package com.intelizign.career.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intelizign.career.model.Admin;
import com.intelizign.career.model.Recruiter;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String email;
	@JsonIgnore
	private String password;
	private List<GrantedAuthority> authorities;

	// Constructor for Admin
	public CustomUserDetails(Admin admin) {
		this.email = admin.getEmail();
		this.password = admin.getPassword();
		this.authorities = List.of(new SimpleGrantedAuthority(admin.getRole().toString()));
	}

	// Constructor for Recruiter
	public CustomUserDetails(Recruiter recruiter) {
		this.email = recruiter.getEmail();
		this.password = recruiter.getPassword();
		this.authorities = List.of(new SimpleGrantedAuthority(recruiter.getRole().toString()));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) 
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomUserDetails user = (CustomUserDetails) o;
		return Objects.equals(id, user.id);
	}
}