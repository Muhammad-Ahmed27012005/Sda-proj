package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.LoginDTO;
import com.sda.project.dto.RegisterDTO;
import com.sda.project.model.User;
import com.sda.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterDTO request, HttpServletRequest servletRequest) {
		User user = userService.register(request);
		Map<String, Object> login = userService.login(user.getEmail(), request.password());
		authenticateSession(user, servletRequest);
		return ApiResponse.ok("Registration successful", login);
	}

	@PostMapping("/login")
	public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginDTO request, HttpServletRequest servletRequest) {
		Map<String, Object> login = userService.login(request.email(), request.password());
		authenticateSession(userService.findByEmail(request.email()), servletRequest);
		return ApiResponse.ok("Login successful", login);
	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout() {
		return ApiResponse.ok("Logged out", null);
	}

	@PostMapping("/forgot-password")
	public ApiResponse<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
		return ApiResponse.ok("Demo reset token generated", Map.of("resetToken", UUID.randomUUID().toString()));
	}

	private void authenticateSession(User user, HttpServletRequest servletRequest) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		servletRequest.getSession(true).setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());
	}
}
