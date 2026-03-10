package com.movietracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbResponse {

    private List<TmdbMovie> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbMovie {

        private Long id;

        private String title;

        private String overview;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("vote_average")
        private Double voteAverage;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;

        public String getFullPosterUrl() {
            if (posterPath != null && !posterPath.isEmpty()) {
                return "https://image.tmdb.org/t/p/w500" + posterPath;
            }
            return "/images/no-poster.png";
        }

        public Integer getReleaseYear() {
            if (releaseDate != null && releaseDate.length() >= 4) {
                try {
                    return Integer.parseInt(releaseDate.substring(0, 4));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbGenre {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbGenreResponse {
        private List<TmdbGenre> genres;
    }
}
