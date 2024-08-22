package com.sparta.schedulemanagement.repository;

import com.sparta.schedulemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
