package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import com.sparta.schedulemanagement.dto.ScheduleResponseDto;
import com.sparta.schedulemanagement.entity.Schedule;
import com.sparta.schedulemanagement.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private Schedule findScheduleById(int id) {
        return scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Schedule not found")
        );
    }

    public ScheduleResponseDto findById(int id) {
        Schedule schedule = findScheduleById(id);
        return new ScheduleResponseDto(schedule);
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new ScheduleResponseDto(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(int id, ScheduleRequestDto requestDto) {
        Schedule schedule = findScheduleById(id);
        schedule.update(requestDto);
        return new ScheduleResponseDto(schedule);
    }

    public List<ScheduleResponseDto> findAllSchedules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedAt").descending());
        Page<ScheduleResponseDto> scheduleResponseDtoPage = scheduleRepository.findAll(pageable).map(ScheduleResponseDto::new);
        return scheduleResponseDtoPage.getContent();
    }

    public String deleteSchedule(int id) {
        scheduleRepository.deleteById(id);
        return "Schedule deleted";
    }
}
