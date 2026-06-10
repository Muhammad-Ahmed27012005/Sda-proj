package com.sda.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
		@NotBlank String fullName,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 6) String password) {
}
