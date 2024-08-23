package com.sparta.schedulemanagement.service;

import com.sparta.schedulemanagement.config.PasswordEncoder;
import com.sparta.schedulemanagement.dto.LoginRequestDto;
import com.sparta.schedulemanagement.dto.SignUpRequestDto;
import com.sparta.schedulemanagement.dto.UserRequestDto;
import com.sparta.schedulemanagement.dto.UserResponseDto;
import com.sparta.schedulemanagement.entity.User;
import com.sparta.schedulemanagement.entity.UserRoleEnum;
import com.sparta.schedulemanagement.jwt.JwtUtil;
import com.sparta.schedulemanagement.repository.UserRepository;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpHeaders;
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

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : users) {
            userResponseDtos.add(new UserResponseDto(user));
        }
        return userResponseDtos;
    }

    public UserResponseDto signUp(SignUpRequestDto signUpRequestDto, HttpServletResponse res) {
        String username = signUpRequestDto.getUsername();
        String email = signUpRequestDto.getEmail();
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if(checkUsername.isPresent()) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        if(signUpRequestDto.isAdmin()) {
            if(!ADMIN_TOKEN.equals(signUpRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("Admin token does not match");
            }
            role = UserRoleEnum.ADMIN;
        }

        String token = jwtUtil.createToken(username, role);
        jwtUtil.addJwtToCookie(token, res);

        User user = new User(username, password, email, role);

        userRepository.save(user);
        return new UserResponseDto(user);
    }

    public UserResponseDto getUserById(int id) {
        return new UserResponseDto(findUserById(id));
    }

    @Transactional
    public UserResponseDto updateUser(int id, @Valid UserRequestDto userRequestDto, HttpServletResponse res, ServletRequest req) throws IOException {
        User user = findUserById(id);

        UserRoleEnum currentRole = (UserRoleEnum) req.getAttribute("user_role");
        if(currentRole == UserRoleEnum.ADMIN) {
            user.update(userRequestDto);
            return new UserResponseDto(user);
        }

        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only ADMIN users can update user");
        return null;
    }

    public String deleteUser(int id, HttpServletResponse res, ServletRequest req) throws IOException {
        UserRoleEnum currentRole = (UserRoleEnum) req.getAttribute("user_role");
        if(currentRole == UserRoleEnum.ADMIN) {
            userRepository.deleteById(id);
            return "User with id " + id + " deleted";
        }
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only ADMIN users can delete user");
        return "Only ADMIN users can delete user";
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

        String token = jwtUtil.createToken(user.get().getUsername(), user.get().getRole());
        jwtUtil.addJwtToCookie(token, res);
        return token;
    }
}
