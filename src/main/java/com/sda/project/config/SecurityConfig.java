package com.sda.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/",
								"/login",
								"/register",
								"/subscribe",
								"/subscribe/success",
								"/css/**",
								"/js/**",
								"/uploads/**",
								"/api/auth/**",
								"/api/videos",
								"/api/videos/",
								"/api/videos/trending",
								"/api/videos/recent",
								"/api/videos/stream/**",
								"/api/videos/stream-info/**",
								"/api/categories/**",
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/error/**")
						.permitAll()
						.requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").usernameParameter("email").defaultSuccessUrl("/", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
