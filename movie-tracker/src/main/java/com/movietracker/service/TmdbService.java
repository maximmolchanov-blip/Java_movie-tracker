package com.movietracker.service;

import com.movietracker.dto.TmdbResponse;
import com.movietracker.entity.Genre;
import com.movietracker.entity.Movie;
import com.movietracker.repository.GenreRepository;
import com.movietracker.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final RestTemplate restTemplate;

    // Cache genre IDs -> names
    private Map<Integer, String> genreCache = new HashMap<>();

    public int importPopularMovies() {
        loadGenres();
        String url = baseUrl + "/movie/popular?api_key=" + apiKey + "&language=en-US&page=1";

        try {
            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);
            if (response == null || response.getResults() == null) {
                log.warn("Empty response from TMDB");
                return 0;
            }

            int count = 0;
            for (TmdbResponse.TmdbMovie tmdbMovie : response.getResults()) {
                if (!movieRepository.existsByTmdbId(tmdbMovie.getId())) {
                    Movie movie = mapToMovie(tmdbMovie);
                    movieRepository.save(movie);
                    count++;
                }
            }
            log.info("Imported {} new movies from TMDB", count);
            return count;
        } catch (Exception e) {
            log.error("Error importing movies from TMDB: {}", e.getMessage());
            throw new RuntimeException("Failed to import movies from TMDB: " + e.getMessage());
        }
    }

    public int importTopRatedMovies() {
        loadGenres();
        String url = baseUrl + "/movie/top_rated?api_key=" + apiKey + "&language=en-US&page=1";

        try {
            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);
            if (response == null || response.getResults() == null) return 0;

            int count = 0;
            for (TmdbResponse.TmdbMovie tmdbMovie : response.getResults()) {
                if (!movieRepository.existsByTmdbId(tmdbMovie.getId())) {
                    Movie movie = mapToMovie(tmdbMovie);
                    movieRepository.save(movie);
                    count++;
                }
            }
            log.info("Imported {} top rated movies from TMDB", count);
            return count;
        } catch (Exception e) {
            log.error("Error importing top rated movies: {}", e.getMessage());
            throw new RuntimeException("Failed to import top rated movies: " + e.getMessage());
        }
    }

    private void loadGenres() {
        if (!genreCache.isEmpty()) return;

        String url = baseUrl + "/genre/movie/list?api_key=" + apiKey + "&language=en-US";
        try {
            TmdbResponse.TmdbGenreResponse response =
                    restTemplate.getForObject(url, TmdbResponse.TmdbGenreResponse.class);

            if (response != null && response.getGenres() != null) {
                for (TmdbResponse.TmdbGenre g : response.getGenres()) {
                    genreCache.put(g.getId(), g.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error loading genres: {}", e.getMessage());
        }
    }

    private Movie mapToMovie(TmdbResponse.TmdbMovie tmdbMovie) {
        Movie movie = new Movie();
        movie.setTmdbId(tmdbMovie.getId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setDescription(tmdbMovie.getOverview());
        movie.setPosterUrl(tmdbMovie.getFullPosterUrl());
        movie.setReleaseYear(tmdbMovie.getReleaseYear());
        movie.setRating(tmdbMovie.getVoteAverage());
        movie.setStatus(Movie.WatchStatus.WANT_TO_WATCH);

        Set<Genre> genres = new HashSet<>();
        if (tmdbMovie.getGenreIds() != null) {
            for (Integer genreId : tmdbMovie.getGenreIds()) {
                String genreName = genreCache.getOrDefault(genreId, "Unknown");
                Genre genre = genreRepository.findByName(genreName)
                        .orElseGet(() -> genreRepository.save(new Genre(genreName)));
                genres.add(genre);
            }
        }
        movie.setGenres(genres);
        return movie;
    }
}
