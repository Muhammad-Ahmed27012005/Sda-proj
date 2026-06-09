package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.model.User;
import com.sda.project.service.AdminService;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/dashboard")
	public ApiResponse<Map<String, Object>> dashboard() {
		return ApiResponse.ok("Dashboard loaded", adminService.dashboard());
	}

	@GetMapping("/users")
	public ApiResponse<List<Map<String, Object>>> users() {
		List<Map<String, Object>> users = adminService.users().stream().map(this::safeUser).toList();
		return ApiResponse.ok("Users loaded", users);
	}

	@PostMapping("/users/{id}/block")
	public ApiResponse<Map<String, Object>> block(@PathVariable Long id) {
		return ApiResponse.ok("User blocked", safeUser(adminService.blockUser(id)));
	}

	@DeleteMapping("/users/{id}")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		adminService.deleteUser(id);
		return ApiResponse.ok("User deleted", null);
	}

	@GetMapping("/reports/revenue")
	public ApiResponse<List<Object[]>> revenue() {
		return ApiResponse.ok("Revenue report loaded", adminService.revenueReport());
	}

	@GetMapping("/reports/most-watched")
	public ApiResponse<Object> mostWatched() {
		return ApiResponse.ok("Most watched report loaded", adminService.mostWatched());
	}

	private Map<String, Object> safeUser(User user) {
		return Map.of(
				"userId", user.getUserId(),
				"fullName", user.getFullName(),
				"email", user.getEmail(),
				"role", user.getRole(),
				"createdAt", user.getCreatedAt());
	}
}
