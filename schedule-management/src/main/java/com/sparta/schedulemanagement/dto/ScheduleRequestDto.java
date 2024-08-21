package com.sparta.schedulemanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScheduleRequestDto {
    @NotNull
    private String username;
    private String title;
    private String content;
}
