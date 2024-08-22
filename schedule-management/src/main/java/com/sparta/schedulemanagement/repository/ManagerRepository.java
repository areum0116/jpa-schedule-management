package com.sparta.schedulemanagement.repository;

import com.sparta.schedulemanagement.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    void deleteByScheduleId(int id);
}
