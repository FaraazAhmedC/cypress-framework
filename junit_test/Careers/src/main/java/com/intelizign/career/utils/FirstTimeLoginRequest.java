package com.intelizign.career.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstTimeLoginRequest {
    private String email;
    private String token;
    private String newPassword;
    
}