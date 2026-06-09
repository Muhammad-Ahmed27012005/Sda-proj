package com.sda.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sda.project.dto.ImdbVideoDTO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImdbApiService {
	private final String apiKey;
	private final String apiHost;
	private final String baseUrl;
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient;

	public ImdbApiService(
			@Value("${rapidapi.key}") String apiKey,
			@Value("${rapidapi.host}") String apiHost,
			@Value("${rapidapi.base-url}") String baseUrl,
			ObjectMapper objectMapper) {
		this.apiKey = apiKey;
		this.apiHost = apiHost;
		this.baseUrl = baseUrl;
		this.objectMapper = objectMapper;
		this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	}

	public ImdbVideoDTO fetchVideoById(String imdbId) {
		JsonNode node = request(baseUrl + "/" + imdbId);
		return mapVideo(node);
	}

	public List<ImdbVideoDTO> fetchTopRated() {
		return fetchList(baseUrl + "/top250");
	}

	public List<ImdbVideoDTO> fetchTrending() {
		return fetchList(baseUrl + "/most-popular-movies");
	}

	private List<ImdbVideoDTO> fetchList(String url) {
		JsonNode node = request(url);
		JsonNode items = node.isArray() ? node : firstExisting(node, "results", "items", "data");
		List<ImdbVideoDTO> videos = new ArrayList<>();
		if (items != null && items.isArray()) {
			for (JsonNode item : items) {
				videos.add(mapVideo(item));
			}
		}
		return videos;
	}

	private JsonNode request(String url) {
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException("RAPIDAPI_KEY is not configured");
		}
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("x-rapidapi-key", apiKey)
				.header("x-rapidapi-host", apiHost)
				.header("Content-Type", "application/json")
				.GET()
				.build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() < 200 || response.statusCode() >= 300) {
				throw new IllegalStateException("IMDb API returned status " + response.statusCode());
			}
			return objectMapper.readTree(response.body());
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to call IMDb API", ex);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("IMDb API request was interrupted", ex);
		}
	}

	private ImdbVideoDTO mapVideo(JsonNode node) {
		return new ImdbVideoDTO(
				text(node, "id", "imdbId", "tconst"),
				text(node, "title", "primaryTitle", "originalTitle"),
				text(node, "plot", "description"),
				stringList(firstExisting(node, "genres", "genre")),
				integer(node, "runtimeMinutes", "runtime"),
				integer(node, "releaseYear", "startYear", "year"),
				decimal(node, "averageRating", "rating"),
				image(node));
	}

	private JsonNode firstExisting(JsonNode node, String... names) {
		for (String name : names) {
			if (node.hasNonNull(name)) {
				return node.get(name);
			}
		}
		return null;
	}

	private String text(JsonNode node, String... names) {
		JsonNode found = firstExisting(node, names);
		return found == null ? null : found.asText();
	}

	private Integer integer(JsonNode node, String... names) {
		JsonNode found = firstExisting(node, names);
		return found == null || !found.canConvertToInt() ? null : found.asInt();
	}

	private Double decimal(JsonNode node, String... names) {
		JsonNode found = firstExisting(node, names);
		return found == null || !found.isNumber() ? null : found.asDouble();
	}

	private List<String> stringList(JsonNode node) {
		if (node == null || node.isNull()) {
			return List.of();
		}
		if (node.isArray()) {
			List<String> values = new ArrayList<>();
			for (JsonNode item : node) {
				values.add(item.asText());
			}
			return values;
		}
		return List.of(node.asText());
	}

	private String image(JsonNode node) {
		JsonNode image = firstExisting(node, "primaryImage", "image");
		if (image == null || image.isNull()) {
			return null;
		}
		if (image.isObject()) {
			JsonNode url = firstExisting(image, "url", "imageUrl");
			return url == null ? null : url.asText();
		}
		return image.asText();
	}
}
