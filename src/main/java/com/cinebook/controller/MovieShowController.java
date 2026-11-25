
package com.cinebook.controller;

import com.cinebook.model.MovieShow;
import com.cinebook.repository.MovieShowRepository;
import com.cinebook.repository.MovieRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class MovieShowController {

    private final MovieShowRepository showRepository;
    private final MovieRepository movieRepository;

    public MovieShowController(
            MovieShowRepository showRepository,
            MovieRepository movieRepository) {

        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
    }

    @PostMapping
    public ResponseEntity<?> addShow(@RequestBody MovieShow show) {

        if (show.getMovieId() == null ||
                !movieRepository.existsById(show.getMovieId())) {
            return ResponseEntity.badRequest().body("Movie not found");
        }

        if (show.getShowDate() == null || show.getShowTime() == null) {
            return ResponseEntity.badRequest()
                    .body("Show date and time are required");
        }

        LocalDateTime showDateTime = LocalDateTime.of(
                show.getShowDate(),
                show.getShowTime()
        );

        if (!showDateTime.isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body("Cannot create a show in the past");
        }

        show.setId(null);

        return ResponseEntity.ok(showRepository.save(show));
    }

    @GetMapping
    public List<MovieShow> getAllShows() {
        return showRepository.findAll();
    }

    @GetMapping("/movie/{movieId}")
    public List<MovieShow> getShowsByMovie(
            @PathVariable Long movieId) {

        return showRepository.findByMovieId(movieId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {

        if (!showRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        showRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
