package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import com.sparta.schedulemanagement.dto.ScheduleResponseDto;
import com.sparta.schedulemanagement.entity.Manager;
import com.sparta.schedulemanagement.entity.Schedule;
import com.sparta.schedulemanagement.entity.User;
import com.sparta.schedulemanagement.repository.ManagerRepository;
import com.sparta.schedulemanagement.repository.ScheduleRepository;
import com.sparta.schedulemanagement.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    private Schedule findScheduleById(int id) {
        return scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Schedule not found")
        );
    }

    private User findUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
    }

    public ScheduleResponseDto getScheduleById(int id) {
        Schedule schedule = findScheduleById(id);
        return new ScheduleResponseDto(schedule);
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        User user = findUserById(requestDto.getUser_id());
        Schedule schedule = scheduleRepository.save(new Schedule(requestDto));
        Manager manager = new Manager();
        manager.setSchedule(schedule);
        manager.setUser(user);
        managerRepository.save(manager);
        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(int id, ScheduleRequestDto requestDto) {
        Schedule schedule = findScheduleById(id);
        schedule.update(requestDto);
        managerRepository.deleteByScheduleId(schedule.getId());
        User user = findUserById(requestDto.getUser_id());
        managerRepository.save(new Manager(schedule, user));
        scheduleRepository.save(schedule);
        return new ScheduleResponseDto(schedule);
    }

    public List<ScheduleResponseDto> getAllSchedules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedAt").descending());
        Page<ScheduleResponseDto> scheduleResponseDtoPage = scheduleRepository.findAll(pageable).map(ScheduleResponseDto::new);
        return scheduleResponseDtoPage.getContent();
    }

    public String deleteSchedule(int id) {
        scheduleRepository.deleteById(id);
        return "Schedule deleted";
    }
}
