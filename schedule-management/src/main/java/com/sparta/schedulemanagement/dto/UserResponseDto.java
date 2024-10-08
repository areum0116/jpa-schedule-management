package com.sparta.schedulemanagement.dto;

import com.sparta.schedulemanagement.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {
    private int id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getLastModifiedAt();
    }

    public static UserResponseDto entityToDto(User user) {
        return new UserResponseDto(user);
    }
}
