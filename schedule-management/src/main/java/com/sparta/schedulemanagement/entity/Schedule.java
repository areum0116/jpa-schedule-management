package com.sparta.schedulemanagement.entity;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Schedule extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int user_id;
    private String title;
    private String content;

    @Setter
    private String weather;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    public final List<Manager> managerList = new ArrayList<>();

    public void addComment(Comment comment) {
        this.commentList.add(comment);
        comment.setSchedule(this);
    }

    public Schedule(ScheduleRequestDto requestDto) {
        this.user_id = requestDto.getUser_id();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.user_id = requestDto.getUser_id();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
}
