package com.sda.project.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/movie")
public class TmdbMovieController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${tmdb.base-url:https://api.themoviedb.org/3}")
    private String tmdbBaseUrl;

    @Value("${vidlink.base-url:https://vidlink.pro/movie/}")
    private String vidlinkBaseUrl;

    @GetMapping("/{tmdbId}")
    public String tmdbMovieDetail(@PathVariable Long tmdbId, org.springframework.ui.Model model) {
        try {
            URI uri = URI.create(tmdbBaseUrl + "/movie/" + tmdbId
                    + "?api_key=" + tmdbApiKey + "&language=en-US&append_to_response=credits,videos,external_ids");
            ResponseEntity<Map> resp = restTemplate.getForEntity(uri, Map.class);
            if (resp.getBody() == null) {
                model.addAttribute("error", "Movie not found");
                return "error";
            }
            Map<String, Object> movie = resp.getBody();
            model.addAttribute("movie", movie);
            model.addAttribute("tmdbId", tmdbId);
            model.addAttribute("vidlinkUrl", vidlinkBaseUrl + tmdbId);
            return "movie-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load movie: " + e.getMessage());
            return "error";
        }
    }
}
