package com.sda.project.service;

import com.sda.project.dto.RegisterDTO;
import com.sda.project.exception.ResourceNotFoundException;
import com.sda.project.exception.UnauthorizedException;
import com.sda.project.model.User;
import com.sda.project.model.enums.Role;
import com.sda.project.repository.UserRepository;
import com.sda.project.util.JwtUtil;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final FileStorageService fileStorageService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, FileStorageService fileStorageService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.fileStorageService = fileStorageService;
	}

	@Transactional
	public User register(RegisterDTO request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("Email is already registered");
		}
		User user = User.builder()
				.fullName(request.fullName())
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.role(Role.USER)
				.build();
		return userRepository.save(user);
	}

	public Map<String, Object> login(String email, String password) {
		User user = findByEmail(email);
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new UnauthorizedException("Invalid email or password");
		}
		String token = jwtUtil.generateToken(user);
		return Map.of(
				"token", token,
				"userId", user.getUserId(),
				"fullName", user.getFullName(),
				"email", user.getEmail(),
				"role", user.getRole());
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	public User currentUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthorizedException("Authentication required");
		}
		return findByEmail(authentication.getName());
	}

	@Transactional
	public User updateProfile(Long userId, String fullName, MultipartFile image) {
		User user = findById(userId);
		if (fullName != null && !fullName.isBlank()) {
			user.setFullName(fullName);
		}
		String profileImage = fileStorageService.store(image, "profiles");
		if (profileImage != null) {
			user.setProfileImage(profileImage);
		}
		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
