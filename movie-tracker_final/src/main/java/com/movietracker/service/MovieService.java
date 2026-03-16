package com.movietracker.service;

import com.movietracker.dto.MovieDto;
import com.movietracker.entity.Movie;
import com.movietracker.entity.Genre;
import com.movietracker.repository.GenreRepository;
import com.movietracker.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;


    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MovieDto> getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(MovieDto::fromEntity);
    }

    public Optional<Movie> getMovieEntityById(Long id) {
        return movieRepository.findById(id);
    }

    public List<MovieDto> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMoviesByStatus(String status) {
        try {
            Movie.WatchStatus watchStatus = Movie.WatchStatus.valueOf(status.toUpperCase());
            return movieRepository.findByStatus(watchStatus).stream()
                    .map(MovieDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public List<MovieDto> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreName(genre).stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<String> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(Genre::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public MovieDto createMovie(MovieDto dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setReleaseYear(dto.getReleaseYear());
        movie.setRating(dto.getRating());
        if (dto.getStatus() != null) {
            movie.setStatus(Movie.WatchStatus.valueOf(dto.getStatus()));
        }
        Movie saved = movieRepository.save(movie);
        return MovieDto.fromEntity(saved);
    }

    public Optional<MovieDto> updateMovie(Long id, MovieDto dto) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(dto.getTitle());
            movie.setDescription(dto.getDescription());
            movie.setPosterUrl(dto.getPosterUrl());
            movie.setReleaseYear(dto.getReleaseYear());
            movie.setRating(dto.getRating());
            if (dto.getStatus() != null) {
                movie.setStatus(Movie.WatchStatus.valueOf(dto.getStatus()));
            }
            if (dto.getGenres() != null) {
                Set<Genre> genres = dto.getGenres().stream()
                        .map(name -> genreRepository.findByName(name)
                                .orElseGet(() -> genreRepository.save(new Genre(name))))
                        .collect(Collectors.toSet());
                movie.setGenres(genres);
            }
            return MovieDto.fromEntity(movieRepository.save(movie));
        });
    }

    public boolean updateStatus(Long id, String status) {
        return movieRepository.findById(id).map(movie -> {
            movie.setStatus(Movie.WatchStatus.valueOf(status.toUpperCase()));
            movieRepository.save(movie);
            return true;
        }).orElse(false);
    }

    public boolean deleteMovie(Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteMovieGenresByMovieId(id);
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long getTotalCount() {
        return movieRepository.count();
    }

    public long getCountByStatus(String status) {
        try {
            return movieRepository.findByStatus(Movie.WatchStatus.valueOf(status)).size();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
