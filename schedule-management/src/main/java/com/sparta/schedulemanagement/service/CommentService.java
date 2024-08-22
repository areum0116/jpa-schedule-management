package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.dto.CommentRequestDto;
import com.sparta.schedulemanagement.dto.CommentResponseDto;
import com.sparta.schedulemanagement.entity.Comment;
import com.sparta.schedulemanagement.entity.Schedule;
import com.sparta.schedulemanagement.repository.CommentRepository;
import com.sparta.schedulemanagement.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;

    private Schedule findScheduleById(int id) {
        return scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Schedule not found")
        );
    }

    private Comment findCommentById(int commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found")
        );
    }

    public List<CommentResponseDto> getAllComments(int scheduleId) {
        Schedule schedule = findScheduleById(scheduleId);
        List<Comment> commentList = commentRepository.findCommentsBySchedule(schedule);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return commentResponseDtoList;
    }

    public CommentResponseDto createComment(int scheduleId, CommentRequestDto commentRequestDto) {
        Schedule schedule = findScheduleById(scheduleId);
        Comment comment = new Comment(commentRequestDto);
        comment.setSchedule(schedule);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    public CommentResponseDto getCommentById(int commentId) {
        return new CommentResponseDto(findCommentById(commentId));
    }

    @Transactional
    public CommentResponseDto updateComment(int scheduleId, int commentId, CommentRequestDto commentRequestDto) {
        Schedule schedule = findScheduleById(scheduleId);
        Comment comment = findCommentById(commentId);
        comment.setSchedule(schedule);
        comment.update(commentRequestDto);
        return new CommentResponseDto(comment);
    }


    public String deleteComment(int scheduleId, int commentId) {
        commentRepository.deleteById(commentId);
        return "Comment deleted";
    }
}
