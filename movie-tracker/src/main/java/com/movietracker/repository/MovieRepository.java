package com.movietracker.repository;

import com.movietracker.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTmdbId(Long tmdbId);

    List<Movie> findByStatus(Movie.WatchStatus status);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName")
    List<Movie> findByGenreName(String genreName);

    boolean existsByTmdbId(Long tmdbId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM movie_genres WHERE movie_id = :movieId", nativeQuery = true)
    void deleteMovieGenresByMovieId(@Param("movieId") Long movieId);
}