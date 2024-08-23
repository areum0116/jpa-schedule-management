package com.sparta.schedulemanagement.controller;

import com.sparta.schedulemanagement.dto.LoginRequestDto;
import com.sparta.schedulemanagement.dto.SignUpRequestDto;
import com.sparta.schedulemanagement.dto.UserRequestDto;
import com.sparta.schedulemanagement.dto.UserResponseDto;
import com.sparta.schedulemanagement.service.UserService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public UserResponseDto signUp(@RequestBody SignUpRequestDto signUpRequestDto, HttpServletResponse res) {
        return userService.signUp(signUpRequestDto, res);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable int id, @RequestBody @Valid UserRequestDto userRequestDto, HttpServletResponse res, ServletRequest req) throws IOException {
        return userService.updateUser(id, userRequestDto, res, req);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id, HttpServletResponse res, ServletRequest req) throws IOException {
        return userService.deleteUser(id, res, req);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res) {
        return userService.login(loginRequestDto, res);
    }
}
