package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.dto.ScheduleRequestDto;
import com.sparta.schedulemanagement.dto.ScheduleResponseDto;
import com.sparta.schedulemanagement.dto.WeatherResponseDto;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final RestTemplate restTemplate;

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

    private WeatherResponseDto getTodayWeather() {
        WeatherResponseDto[] responseDtos = restTemplate.getForObject("https://f-api.github.io/f-api/weather.json", WeatherResponseDto[].class);
        List<WeatherResponseDto> weatherList = Arrays.asList(responseDtos);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
        Optional<WeatherResponseDto> todayWeather = weatherList.stream()
                .filter(weather -> today.equals(weather.getDate())).findFirst();
        return todayWeather.orElse(null);
    }

    public ScheduleResponseDto getScheduleById(int id) {
        Schedule schedule = findScheduleById(id);
        User user = findUserById(schedule.getUser_id());
        return new ScheduleResponseDto(schedule, user);
    }


    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        User user = findUserById(requestDto.getUser_id());
        Schedule schedule = new Schedule(requestDto);

        WeatherResponseDto todayWeather = getTodayWeather();
        schedule.setWeather(todayWeather.getWeather());
        schedule = scheduleRepository.save(schedule);

        Manager manager = new Manager(schedule, user);
        managerRepository.save(manager);

        return ScheduleResponseDto.entityToDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(int id, ScheduleRequestDto requestDto) {
        Schedule schedule = findScheduleById(id);
        schedule.update(requestDto);
        managerRepository.deleteByScheduleId(schedule.getId());
        User user = findUserById(requestDto.getUser_id());
        managerRepository.save(new Manager(schedule, user));
        scheduleRepository.save(schedule);
        return ScheduleResponseDto.entityToDto(schedule);
    }

    public List<ScheduleResponseDto> getScheduleList(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedAt").descending());
        Page<ScheduleResponseDto> scheduleResponseDtoPage = scheduleRepository.findAll(pageable).map(ScheduleResponseDto::new);
        return scheduleResponseDtoPage.getContent();
    }

    public String deleteSchedule(int id) {
        scheduleRepository.deleteById(id);
        return "Schedule deleted";
    }
}
