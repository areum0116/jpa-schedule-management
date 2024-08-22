package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.dto.UserRequestDto;
import com.sparta.schedulemanagement.dto.UserResponseDto;
import com.sparta.schedulemanagement.entity.User;
import com.sparta.schedulemanagement.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private User findUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User with id " + id + " not found")
        );
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : users) {
            userResponseDtos.add(new UserResponseDto(user));
        }
        return userResponseDtos;
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User savedUser = userRepository.save(new User(userRequestDto));
        return new UserResponseDto(savedUser);
    }

    public UserResponseDto getUserById(int id) {
        return new UserResponseDto(findUserById(id));
    }

    @Transactional
    public UserResponseDto updateUser(int id, @Valid UserRequestDto userRequestDto) {
        User user = findUserById(id);
        user.update(userRequestDto);
        return new UserResponseDto(user);
    }

    public String deleteUser(int id) {
        userRepository.deleteById(id);
        return "User with id " + id + " deleted";
    }
}
