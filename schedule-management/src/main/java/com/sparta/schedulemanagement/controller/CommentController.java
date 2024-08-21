package com.sparta.schedulemanagement.controller;

import com.sparta.schedulemanagement.dto.CommentRequestDto;
import com.sparta.schedulemanagement.dto.CommentResponseDto;
import com.sparta.schedulemanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("schedules/{id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable int id) {
        return commentService.getComments(id);
    }

    @PostMapping
    public CommentResponseDto createComment(@PathVariable int id, @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(id, commentRequestDto);
    }

    @GetMapping("/{comment_id}")
    public CommentResponseDto getCommentById(@PathVariable int id, @PathVariable int comment_id) {
        return commentService.getCommentById(comment_id);
    }

    @PutMapping("/{comment_id}")
    public CommentResponseDto updateComment(@PathVariable int id ,@PathVariable int comment_id, @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.updateComment(id, comment_id, commentRequestDto);
    }

    @DeleteMapping("/{comment_id}")
    public String deleteComment(@PathVariable int id, @PathVariable int comment_id) {
        return commentService.deleteComment(id, comment_id);
    }
}
