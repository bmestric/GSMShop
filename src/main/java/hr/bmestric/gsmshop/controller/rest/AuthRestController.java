package hr.bmestric.gsmshop.controller.rest;

import hr.bmestric.gsmshop.dto.ApiError;
import hr.bmestric.gsmshop.dto.AuthResponse;
import hr.bmestric.gsmshop.dto.LoginRequest;
import hr.bmestric.gsmshop.dto.RefreshRequest;
import hr.bmestric.gsmshop.dto.RegisterRequest;
import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.enums.Role;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        Optional<AppUser> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password"));
        }
        AppUser user = userOpt.get();
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(error(HttpStatus.FORBIDDEN.value(), "Account is disabled"));
        }
        return ResponseEntity.ok(buildAuthResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error(HttpStatus.CONFLICT.value(), "Email already registered"));
        }
        AppUser user = new AppUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildAuthResponse(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@Valid @RequestBody RefreshRequest request) {
        String token = request.getRefreshToken();
        if (!jwtUtil.isTokenValid(token) || !jwtUtil.isRefreshToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token"));
        }
        String email = jwtUtil.extractEmail(token);
        AppUser user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error(HttpStatus.UNAUTHORIZED.value(), "User not found or disabled"));
        }
        String newAccessToken = jwtUtil.generateAccessToken(email, user.getRole().name());
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .email(email)
                .role(user.getRole().name())
                .build());
    }

    private AuthResponse buildAuthResponse(AppUser user) {
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getEmail()))
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private ApiError error(int status, String message) {
        return ApiError.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
