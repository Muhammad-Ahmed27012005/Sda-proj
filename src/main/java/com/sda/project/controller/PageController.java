package com.sda.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/subscribe")
	public String subscribe() {
		return "subscription";
	}

	@GetMapping("/videos/{id}")
	public String videoDetail(@PathVariable Long id) {
		return "video-detail";
	}

	@GetMapping("/watch/{id}")
	public String watch(@PathVariable Long id) {
		return "video-player";
	}

	@GetMapping("/watchlist")
	public String watchlist() {
		return "watchlist";
	}

	@GetMapping("/profile")
	public String profile() {
		return "profile";
	}

	@GetMapping("/history")
	public String history() {
		return "history";
	}

	@GetMapping("/admin")
	public String adminDashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/admin/videos")
	public String adminVideos() {
		return "admin/videos";
	}

	@GetMapping("/admin/users")
	public String adminUsers() {
		return "admin/users";
	}
}
