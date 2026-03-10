package com.movietracker.controller;

import com.movietracker.dto.MovieDto;
import com.movietracker.service.MovieService;
import com.movietracker.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieRestController {

    private final MovieService movieService;
    private final TmdbService tmdbService;

    // GET all movies
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre) {

        List<MovieDto> movies;

        if (search != null && !search.isBlank()) {
            movies = movieService.searchMovies(search);
        } else if (status != null && !status.isBlank()) {
            movies = movieService.getMoviesByStatus(status);
        } else if (genre != null && !genre.isBlank()) {
            movies = movieService.getMoviesByGenre(genre);
        } else {
            movies = movieService.getAllMovies();
        }

        return ResponseEntity.ok(movies);
    }

    // GET single movie
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE movie manually
    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto dto) {
        MovieDto created = movieService.createMovie(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // UPDATE movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id, @RequestBody MovieDto dto) {
        return movieService.updateMovie(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
        }

        boolean updated = movieService.updateStatus(id, status);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMovie(@PathVariable Long id) {
        boolean deleted = movieService.deleteMovie(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // IMPORT popular movies from TMDB
    @PostMapping("/import/popular")
    public ResponseEntity<Map<String, Object>> importPopular() {
        try {
            int count = tmdbService.importPopularMovies();
            return ResponseEntity.ok(Map.of(
                    "message", "Import successful",
                    "imported", count
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // IMPORT top rated movies from TMDB
    @PostMapping("/import/top-rated")
    public ResponseEntity<Map<String, Object>> importTopRated() {
        try {
            int count = tmdbService.importTopRatedMovies();
            return ResponseEntity.ok(Map.of(
                    "message", "Import successful",
                    "imported", count
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET all genres
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(movieService.getAllGenres());
    }

    // GET stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", movieService.getTotalCount(),
                "watched", movieService.getCountByStatus("WATCHED"),
                "watching", movieService.getCountByStatus("WATCHING"),
                "wantToWatch", movieService.getCountByStatus("WANT_TO_WATCH"),
                "dropped", movieService.getCountByStatus("DROPPED")
        ));
    }
}
