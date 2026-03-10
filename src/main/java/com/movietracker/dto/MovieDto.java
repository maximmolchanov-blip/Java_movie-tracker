package com.movietracker.dto;

import com.movietracker.entity.Genre;
import com.movietracker.entity.Movie;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Long id;
    private String title;
    private String description;
    private String posterUrl;
    private Integer releaseYear;
    private Double rating;
    private Long tmdbId;
    private String status;
    private Set<String> genres;

    public static MovieDto fromEntity(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setRating(movie.getRating());
        dto.setTmdbId(movie.getTmdbId());
        dto.setStatus(movie.getStatus() != null ? movie.getStatus().name() : null);
        dto.setGenres(movie.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
}
