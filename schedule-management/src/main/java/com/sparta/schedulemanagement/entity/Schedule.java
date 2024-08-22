package com.sparta.schedulemanagement.entity;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor
public class Schedule extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int user_id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Manager> managerList = new ArrayList<>();

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
