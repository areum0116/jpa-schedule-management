package com.sparta.schedulemanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.schedulemanagement.entity.Schedule;
import com.sparta.schedulemanagement.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDto {
    private int id;
    private int user_id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int commentsCount;

    private String username;
    private String email;

    private String weather;

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.user_id = schedule.getUser_id();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.createdAt = schedule.getCreatedAt();
        this.modifiedAt = schedule.getLastModifiedAt();
        this.commentsCount = schedule.getCommentList().size();
        this.weather = schedule.getWeather();
    }

    public ScheduleResponseDto(Schedule schedule, User user) {
        this(schedule);
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
