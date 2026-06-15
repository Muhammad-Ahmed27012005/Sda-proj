package com.sda.project.controller;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Proxies Watchmode API requests server-side so the API key is never exposed
 * to the browser.
 *
 * Endpoints:
 *   GET /api/watchmode/search?q={query}
 *   GET /api/watchmode/title/{id}
 *   GET /api/watchmode/title/{id}/sources
 */
@RestController
@RequestMapping("/api/watchmode")
public class WatchmodeController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${watchmode.api-key}")
    private String apiKey;

    @Value("${watchmode.base-url}")
    private String baseUrl;

    // ── Search by title name ──────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("q") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query parameter 'q' is required"));
        }

        try {
            // Build URL manually — safe because we control all parts and encode the user query
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            URI uri = URI.create(baseUrl + "/search/"
                    + "?apiKey=" + apiKey
                    + "&search_field=name"
                    + "&search_value=" + encodedQuery);

            ResponseEntity<Object> upstream = restTemplate.getForEntity(uri, Object.class);
            return ResponseEntity.status(upstream.getStatusCode()).body(upstream.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Watchmode API error: " + e.getMessage()));
        }
    }

    // ── Title details ─────────────────────────────────────────────────────
    @GetMapping("/title/{id}")
    public ResponseEntity<?> titleDetails(@PathVariable String id) {
        try {
            URI uri = URI.create(baseUrl + "/title/" + id + "/details/?apiKey=" + apiKey);
            ResponseEntity<Object> upstream = restTemplate.getForEntity(uri, Object.class);
            return ResponseEntity.status(upstream.getStatusCode()).body(upstream.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Watchmode API error: " + e.getMessage()));
        }
    }

    // ── Streaming sources for a title ─────────────────────────────────────
    @GetMapping("/title/{id}/sources")
    public ResponseEntity<?> titleSources(@PathVariable String id) {
        try {
            URI uri = URI.create(baseUrl + "/title/" + id + "/sources/?apiKey=" + apiKey);
            ResponseEntity<Object> upstream = restTemplate.getForEntity(uri, Object.class);
            return ResponseEntity.status(upstream.getStatusCode()).body(upstream.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Watchmode API error: " + e.getMessage()));
        }
    }
}
