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

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;

    @GetMapping("/configuration")
    public ResponseEntity<?> configuration() {
        try {
            URI uri = URI.create(baseUrl + "/configuration?api_key=" + apiKey);
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/genre/movie/list")
    public ResponseEntity<?> movieGenres() {
        try {
            URI uri = URI.create(baseUrl + "/genre/movie/list?api_key=" + apiKey + "&language=en-US");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/search/movie")
    public ResponseEntity<?> searchMovie(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query parameter 'query' is required"));
        }
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            URI uri = URI.create(baseUrl + "/search/movie?api_key=" + apiKey
                    + "&query=" + encodedQuery + "&include_adult=false&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/trending/movie/week")
    public ResponseEntity<?> trendingMovies() {
        try {
            URI uri = URI.create(baseUrl + "/trending/movie/week?api_key=" + apiKey + "&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/trending/tv/week")
    public ResponseEntity<?> trendingTv() {
        try {
            URI uri = URI.create(baseUrl + "/trending/tv/week?api_key=" + apiKey + "&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/trending/{type}/{timeWindow}")
    public ResponseEntity<?> trending(@PathVariable String type, @PathVariable String timeWindow) {
        if (!type.equals("movie") && !type.equals("tv") && !type.equals("all")) {
            return ResponseEntity.badRequest().body(Map.of("error", "type must be: movie, tv, or all"));
        }
        if (!timeWindow.equals("day") && !timeWindow.equals("week")) {
            return ResponseEntity.badRequest().body(Map.of("error", "timeWindow must be: day or week"));
        }
        try {
            URI uri = URI.create(baseUrl + "/trending/" + type + "/" + timeWindow
                    + "?api_key=" + apiKey + "&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/top_rated")
    public ResponseEntity<?> topRatedMovies() {
        try {
            URI uri = URI.create(baseUrl + "/movie/top_rated?api_key=" + apiKey + "&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/now_playing")
    public ResponseEntity<?> nowPlaying() {
        try {
            URI uri = URI.create(baseUrl + "/movie/now_playing?api_key=" + apiKey
                    + "&language=en-US&page=1&region=US");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/upcoming")
    public ResponseEntity<?> upcoming() {
        try {
            URI uri = URI.create(baseUrl + "/movie/upcoming?api_key=" + apiKey
                    + "&language=en-US&page=1&region=US");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/discover/movie")
    public ResponseEntity<?> discoverMovies(
            @RequestParam(required = false) Integer genre,
            @RequestParam(defaultValue = "popularity.desc") String sortBy,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "1000") Integer voteCountGte) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl).append("/discover/movie?api_key=").append(apiKey);
            sb.append("&language=en-US&include_adult=false&sort_by=").append(sortBy);
            sb.append("&page=").append(page);
            sb.append("&vote_count.gte=").append(voteCountGte);
            if (genre != null && genre > 0) {
                sb.append("&with_genres=").append(genre);
            }
            URI uri = URI.create(sb.toString());
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<?> movieDetail(@PathVariable Long id) {
        try {
            URI uri = URI.create(baseUrl + "/movie/" + id
                    + "?api_key=" + apiKey + "&language=en-US&append_to_response=credits,videos,external_ids");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/{id}/videos")
    public ResponseEntity<?> movieVideos(@PathVariable Long id) {
        try {
            URI uri = URI.create(baseUrl + "/movie/" + id + "/videos?api_key=" + apiKey + "&language=en-US");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/tv/{id}")
    public ResponseEntity<?> tvDetail(@PathVariable Long id) {
        try {
            URI uri = URI.create(baseUrl + "/tv/" + id
                    + "?api_key=" + apiKey + "&language=en-US&append_to_response=credits,videos,external_ids");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/search/multi")
    public ResponseEntity<?> searchMulti(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query parameter 'query' is required"));
        }
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            URI uri = URI.create(baseUrl + "/search/multi?api_key=" + apiKey
                    + "&query=" + encodedQuery + "&include_adult=false&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/movie/{id}/images")
    public ResponseEntity<?> movieImages(@PathVariable Long id) {
        try {
            URI uri = URI.create(baseUrl + "/movie/" + id + "/images?api_key=" + apiKey);
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }

    @GetMapping("/enrich")
    public ResponseEntity<?> enrichByTitle(@RequestParam("title") String title) {
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query parameter 'title' is required"));
        }
        try {
            String encodedQuery = java.net.URLEncoder.encode(title, java.nio.charset.StandardCharsets.UTF_8);
            URI uri = URI.create(baseUrl + "/search/movie?api_key=" + apiKey
                    + "&query=" + encodedQuery + "&include_adult=false&language=en-US&page=1");
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "TMDB API error: " + e.getMessage()));
        }
    }
}
