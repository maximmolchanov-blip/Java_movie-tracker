package com.movietracker.controller;

import com.movietracker.dto.MovieDto;
import com.movietracker.entity.Movie;
import com.movietracker.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre,
            Model model) {

        List<MovieDto> movies;

        if (search != null && !search.isBlank()) {
            movies = movieService.searchMovies(search);
            model.addAttribute("currentSearch", search);
        } else if (status != null && !status.isBlank()) {
            movies = movieService.getMoviesByStatus(status);
            model.addAttribute("currentStatus", status);
        } else if (genre != null && !genre.isBlank()) {
            movies = movieService.getMoviesByGenre(genre);
            model.addAttribute("currentGenre", genre);
        } else {
            movies = movieService.getAllMovies();
        }

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", movieService.getTotalCount());
        stats.put("watched", movieService.getCountByStatus("WATCHED"));
        stats.put("watching", movieService.getCountByStatus("WATCHING"));
        stats.put("wantToWatch", movieService.getCountByStatus("WANT_TO_WATCH"));

        model.addAttribute("movies", movies);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("stats", stats);
        model.addAttribute("statuses", Movie.WatchStatus.values());

        return "index";
    }

    @GetMapping("/movies/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        return movieService.getMovieById(id)
                .map(movie -> {
                    model.addAttribute("movie", movie);
                    model.addAttribute("statuses", Movie.WatchStatus.values());
                    return "movie-detail";
                })
                .orElse("redirect:/");
    }

    @PostMapping("/movies/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        movieService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Статус оновлено!");
        return "redirect:/movies/" + id;
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        movieService.deleteMovie(id);
        redirectAttributes.addFlashAttribute("success", "Фільм видалено!");
        return "redirect:/";
    }
}
