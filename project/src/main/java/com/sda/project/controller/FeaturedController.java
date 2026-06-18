package com.sda.project.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
@RequestMapping("/api/videos/tmdb")
public class FeaturedController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${tmdb.base-url:https://api.themoviedb.org/3}")
    private String tmdbBaseUrl;

    @Value("${tmdb.image-base-url:https://image.tmdb.org/t/p}")
    private String imageBaseUrl;

    private static final String IMAGE_SIZE = "w500";

    private String posterUrl(String path) {
        if (path == null || path.isBlank()) return null;
        return imageBaseUrl + "/" + IMAGE_SIZE + path;
    }

    private String year(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 4) return "—";
        return releaseDate.substring(0, 4);
    }

    private java.util.Map<String, Object> transformMovie(Map<String, Object> m) {
        java.util.Map<String, Object> out = new LinkedHashMap<>();
        out.put("title", m.get("title"));
        out.put("year", year((String) m.get("release_date")));
        out.put("genre", mapGenreIds((List<Integer>) m.get("genre_ids")));
        out.put("rating", m.get("vote_average") instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : null);
        out.put("poster", posterUrl((String) m.get("poster_path")));
        out.put("desc", m.get("overview"));
        out.put("id", m.get("id"));
        out.put("backdrop", posterUrl((String) m.get("backdrop_path")));
        out.put("tmdbId", m.get("id"));
        return out;
    }

    private String mapGenreIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return "Movie";
        return switch (ids.get(0)) {
            case 28 -> "action";
            case 12 -> "adventure";
            case 16 -> "animation";
            case 35 -> "comedy";
            case 80 -> "crime";
            case 99 -> "documentary";
            case 18 -> "drama";
            case 10751 -> "family";
            case 14 -> "fantasy";
            case 36 -> "history";
            case 27 -> "horror";
            case 10402 -> "music";
            case 9648 -> "mystery";
            case 10749 -> "romance";
            case 878 -> "scifi";
            case 10770 -> "tv";
            case 53 -> "thriller";
            case 10752 -> "war";
            case 37 -> "western";
            default -> "Movie";
        };
    }

    @GetMapping("/famous-movies")
    public ResponseEntity<?> famousMovies(
            @RequestParam(required = false, defaultValue = "all") String genre) {
        String genreId = switch (genre.toLowerCase()) {
            case "action"    -> "28";
            case "drama"     -> "18";
            case "scifi"     -> "878";
            case "animation" -> "16";
            case "horror"    -> "27";
            case "all"       -> null;
            default          -> null;
        };
        List<Map<String, Object>> results = discoverMovies(genreId, 1);
        return ResponseEntity.ok(Map.of(
                "results", results,
                "genre", genre,
                "totalResults", results.size()
        ));
    }

    private List<Map<String, Object>> discoverMovies(String genreId, int page) {
        try {
            StringBuilder url = new StringBuilder();
            url.append(tmdbBaseUrl)
               .append("/discover/movie?api_key=").append(tmdbApiKey)
               .append("&language=en-US&include_adult=false")
               .append("&sort_by=popularity.desc&page=").append(page)
               .append("&vote_count.gte=100");
            if (genreId != null && !genreId.isBlank()) {
                url.append("&with_genres=").append(genreId);
            }
            URI uri = URI.create(url.toString());
            ResponseEntity<Map> resp = restTemplate.getForEntity(uri, Map.class);
            if (resp.getBody() == null) return List.of();
            List<Map<String, Object>> results = (List<Map<String, Object>>) resp.getBody().getOrDefault("results", List.of());
            List<Map<String, Object>> mapped = new ArrayList<>();
            for (Map<String, Object> m : results) {
                mapped.add(transformMovie(m));
            }
            return mapped;
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/home-data")
    public ResponseEntity<?> homePageData() {
        java.util.Map<String, Object> data = new LinkedHashMap<>();

        try {
            URI trendingUri = URI.create(tmdbBaseUrl + "/trending/movie/week?api_key=" + tmdbApiKey + "&language=en-US&page=1");
            ResponseEntity<Map> trendingResp = restTemplate.getForEntity(trendingUri, Map.class);
            List<Map<String, Object>> trending = new ArrayList<>();
            if (trendingResp.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) trendingResp.getBody().getOrDefault("results", List.of());
                for (Map<String, Object> m : results) {
                    trending.add(transformHomeMovie(m));
                }
            }
            data.put("trending", trending);
        } catch (Exception e) {
            data.put("trending", List.of());
        }

        try {
            URI topRatedUri = URI.create(tmdbBaseUrl + "/movie/top_rated?api_key=" + tmdbApiKey + "&language=en-US&page=1");
            ResponseEntity<Map> topResp = restTemplate.getForEntity(topRatedUri, Map.class);
            List<Map<String, Object>> topRated = new ArrayList<>();
            if (topResp.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) topResp.getBody().getOrDefault("results", List.of());
                for (Map<String, Object> m : results) {
                    topRated.add(transformHomeMovie(m));
                }
            }
            data.put("topRated", topRated);
        } catch (Exception e) {
            data.put("topRated", List.of());
        }

        try {
            URI recentUri = URI.create(tmdbBaseUrl + "/movie/now_playing?api_key=" + tmdbApiKey + "&language=en-US&page=1&region=US");
            ResponseEntity<Map> recentResp = restTemplate.getForEntity(recentUri, Map.class);
            List<Map<String, Object>> recent = new ArrayList<>();
            if (recentResp.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) recentResp.getBody().getOrDefault("results", List.of());
                for (Map<String, Object> m : results) {
                    recent.add(transformHomeMovie(m));
                }
            }
            data.put("recent", recent);
        } catch (Exception e) {
            data.put("recent", List.of());
        }

        try {
            URI featuredUri = URI.create(tmdbBaseUrl + "/discover/movie?api_key=" + tmdbApiKey
                    + "&language=en-US&include_adult=false&sort_by=popularity.desc&page=1&vote_count.gte=1000");
            ResponseEntity<Map> featResp = restTemplate.getForEntity(featuredUri, Map.class);
            List<Map<String, Object>> featured = new ArrayList<>();
            if (featResp.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) featResp.getBody().getOrDefault("results", List.of());
                for (Map<String, Object> m : results) {
                    featured.add(transformHomeMovie(m));
                }
            }
            data.put("featured", featured);
        } catch (Exception e) {
            data.put("featured", List.of());
        }

        return ResponseEntity.ok(data);
    }

    private java.util.Map<String, Object> transformHomeMovie(Map<String, Object> m) {
        java.util.Map<String, Object> out = new LinkedHashMap<>();
        Object id = m.get("id");
        out.put("videoId", "tmdb-" + id);
        out.put("tmdbId", id);
        out.put("mediaType", "movie");
        out.put("title", m.get("title"));
        out.put("year", year((String) m.get("release_date")));
        out.put("genre", mapGenreIds((List<Integer>) m.get("genre_ids")));
        out.put("rating", m.get("vote_average") instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : null);
        out.put("poster", posterUrl((String) m.get("poster_path")));
        out.put("backdrop", posterUrl((String) m.get("backdrop_path")));
        out.put("thumbnailUrl", posterUrl((String) m.get("poster_path")));
        out.put("desc", m.get("overview"));
        out.put("description", m.get("overview"));
        out.put("releaseYear", year((String) m.get("release_date")));
        out.put("duration", null);
        out.put("videoUrl", null);
        out.put("imdbId", null);
        out.put("uploadDate", null);
        out.put("popularity", m.get("popularity"));
        out.put("voteCount", m.get("vote_count"));
        return out;
    }
}
