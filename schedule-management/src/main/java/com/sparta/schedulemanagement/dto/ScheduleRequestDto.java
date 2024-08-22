package com.sparta.schedulemanagement.dto;

import lombok.Getter;

@Getter
public class ScheduleRequestDto {
    private int user_id;
    private String title;
    private String content;
}
