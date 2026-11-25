
package com.cinebook.controller;

import com.cinebook.model.Movie;
import com.cinebook.repository.MovieRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    private final Path uploadDirectory =
            Paths.get("uploads").toAbsolutePath().normalize();

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Existing JSON movie addition
    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        movie.setId(null);
        return movieRepository.save(movie);
    }

    // New poster upload movie addition
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addMovieWithPoster(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String genre,
            @RequestParam String language,
            @RequestParam int duration,
            @RequestParam("poster") MultipartFile poster) {

        try {
            if (poster.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a poster.");
            }

            if (poster.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Poster must be 5 MB or smaller.");
            }

            String contentType = poster.getContentType();

            if (contentType == null ||
                    !List.of("image/jpeg", "image/png", "image/webp", "image/gif")
                            .contains(contentType)) {
                return ResponseEntity.badRequest()
                        .body("Only JPG, PNG, WEBP and GIF images are allowed.");
            }

            String extension = switch (contentType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> throw new IllegalArgumentException("Invalid image type");
            };

            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadDirectory.resolve(fileName).normalize();

            if (!filePath.startsWith(uploadDirectory)) {
                return ResponseEntity.badRequest().body("Invalid file name.");
            }

            Files.copy(poster.getInputStream(), filePath);

            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setGenre(genre);
            movie.setLanguage(language);
            movie.setDuration(duration);
            movie.setPosterUrl("/uploads/" + fileName);

            return ResponseEntity.ok(movieRepository.save(movie));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Unable to save poster image.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @RequestBody Movie updatedMovie) {

        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setTitle(updatedMovie.getTitle());
                    movie.setDescription(updatedMovie.getDescription());
                    movie.setGenre(updatedMovie.getGenre());
                    movie.setLanguage(updatedMovie.getLanguage());
                    movie.setDuration(updatedMovie.getDuration());
                    movie.setPosterUrl(updatedMovie.getPosterUrl());

                    return ResponseEntity.ok(movieRepository.save(movie));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
