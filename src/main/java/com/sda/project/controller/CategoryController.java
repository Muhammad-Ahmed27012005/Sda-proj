package com.sda.project.controller;

import com.sda.project.dto.ApiResponse;
import com.sda.project.patterns.composite.ContentComponent;
import com.sda.project.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping({"", "/"})
	public ApiResponse<ContentComponent> tree() {
		return ApiResponse.ok("Category tree loaded", categoryService.categoryTree());
	}
}
