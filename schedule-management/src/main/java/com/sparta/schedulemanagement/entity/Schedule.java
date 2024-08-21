package com.sparta.schedulemanagement.entity;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor
public class Schedule extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;

    public Schedule(ScheduleRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
}
