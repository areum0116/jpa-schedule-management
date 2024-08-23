package com.sparta.schedulemanagement.dto;

import lombok.Getter;

@Getter
public class SignUpRequestDto {
    private String username;
    private String email;
    private String password;
    private boolean admin = false;
    private String adminToken = "";
}
