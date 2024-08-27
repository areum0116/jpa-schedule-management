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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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

    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

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
        return UserResponseDto.entityToDto(user);
    }

    public UserResponseDto getUserById(int id) {
        return UserResponseDto.entityToDto(findUserById(id));
    }

    @Transactional
    public UserResponseDto updateUser(int id, @Valid UserRequestDto userRequestDto, ServletRequest req) throws IOException {
        User user = findUserById(id);

        UserRoleEnum currentRole = (UserRoleEnum) req.getAttribute("user_role");
        if(currentRole == UserRoleEnum.ADMIN) {
            user.update(userRequestDto);
            return new UserResponseDto(user);
        }
        else throw new AccessDeniedException("Only ADMIN users can update user");
    }

    public String deleteUser(int id, ServletRequest req) throws IOException {
        UserRoleEnum currentRole = (UserRoleEnum) req.getAttribute("user_role");
        if(currentRole == UserRoleEnum.ADMIN) {
            userRepository.deleteById(id);
            return "User with id " + id + " deleted";
        }
        else throw new AccessDeniedException("Only ADMIN users can delete user");
    }

    public String login(LoginRequestDto loginRequestDto, HttpServletResponse res) throws AuthenticationException {
        Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());

        if(user.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        String token = jwtUtil.createToken(user.get().getUsername(), user.get().getRole());
        jwtUtil.addJwtToCookie(token, res);
        return token;
    }
}
