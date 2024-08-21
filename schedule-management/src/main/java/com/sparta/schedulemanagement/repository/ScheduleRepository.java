package com.sparta.schedulemanagement.repository;

import com.sparta.schedulemanagement.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}
