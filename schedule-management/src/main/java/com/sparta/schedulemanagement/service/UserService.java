package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.config.PasswordEncoder;
import com.sparta.schedulemanagement.dto.LoginRequestDto;
import com.sparta.schedulemanagement.dto.UserRequestDto;
import com.sparta.schedulemanagement.dto.UserResponseDto;
import com.sparta.schedulemanagement.entity.User;
import com.sparta.schedulemanagement.jwt.JwtUtil;
import com.sparta.schedulemanagement.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

    public UserResponseDto signUp(UserRequestDto userRequestDto, HttpServletResponse res) {
        String username = userRequestDto.getUsername();
        String email = userRequestDto.getEmail();
        String password = passwordEncoder.encode(userRequestDto.getPassword());

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if(checkUsername.isPresent()) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        String token = jwtUtil.createToken(username);
        jwtUtil.addJwtToCookie(token, res);

        User user = new User(userRequestDto);
        user.setEncodedPassword(password);
        userRepository.save(user);
        return new UserResponseDto(user);
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

    public String login(LoginRequestDto loginRequestDto, HttpServletResponse res) {
        Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());

        if(user.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid email or password";
        }

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid email or password";
        }

        String token = jwtUtil.createToken(user.get().getUsername());
        jwtUtil.addJwtToCookie(token, res);
        return token;
    }
}
