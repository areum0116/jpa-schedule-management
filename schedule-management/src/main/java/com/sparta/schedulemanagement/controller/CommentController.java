package com.sparta.schedulemanagement.controller;

import com.sparta.schedulemanagement.dto.CommentRequestDto;
import com.sparta.schedulemanagement.dto.CommentResponseDto;
import com.sparta.schedulemanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("schedules/{id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getCommentList(@PathVariable int id) {
        return new ResponseEntity<>(commentService.getCommentList(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable int id, @RequestBody CommentRequestDto commentRequestDto) {
        return new ResponseEntity<>(commentService.createComment(id, commentRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{comment_id}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable int comment_id) {
        return ResponseEntity.ok(commentService.getCommentById(comment_id));
    }

    @PutMapping("/{comment_id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable int id ,@PathVariable int comment_id, @RequestBody CommentRequestDto commentRequestDto) {
        return ResponseEntity.ok(commentService.updateComment(id, comment_id, commentRequestDto));
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable int comment_id) {
        return ResponseEntity.ok(commentService.deleteComment(comment_id));
    }
}
