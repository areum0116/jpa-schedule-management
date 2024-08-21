package com.sparta.schedulemanagement.controller;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import com.sparta.schedulemanagement.dto.ScheduleResponseDto;
import com.sparta.schedulemanagement.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/{id}")
    public ScheduleResponseDto findById(@PathVariable int id) {
        return scheduleService.findById(id);
    }

    @PostMapping
    public ScheduleResponseDto createSchedule(@RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.createSchedule(requestDto);
    }

    @PutMapping("/{id}")
    public ScheduleResponseDto updateSchedule(@PathVariable int id, @RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.updateSchedule(id, requestDto);
    }
}
