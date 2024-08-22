package com.sparta.schedulemanagement.dto;

import com.sparta.schedulemanagement.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponseDto {
    private int id;
    private int user_id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int commentsCount;

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.user_id = schedule.getUser_id();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.createdAt = schedule.getCreatedAt();
        this.modifiedAt = schedule.getLastModifiedAt();
        this.commentsCount = schedule.getCommentList().size();
    }
}
