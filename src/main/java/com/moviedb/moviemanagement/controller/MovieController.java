package com.moviedb.moviemanagement.controller;

import com.moviedb.dto.MovieDTOs.*;
import com.moviedb.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.moviedb.dto.MovieDTOs.MovieRequest;
import com.moviedb.dto.MovieDTOs.MovieResponse;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(movieService.getMovieById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Search with name, genre, language, minRating
    // Removed: fromDate, toDate
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(movieService.searchMovies(name, genre, language, minRating));
    }

    // NEW: Latest movies sorted by release date
    @GetMapping("/latest")
    public ResponseEntity<List<MovieResponse>> getLatestMovies() {
        return ResponseEntity.ok(movieService.getLatestMovies());
    }

    // NEW: Movies by month and year  e.g. /api/movies/by-month?month=3&year=2025
    @GetMapping("/by-month")
    public ResponseEntity<?> getMoviesByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body("Month must be between 1 and 12");
        }
        return ResponseEntity.ok(movieService.getMoviesByMonthAndYear(month, year));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMovie(@Valid @RequestBody MovieRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movieService.addMovie(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        try {
            return ResponseEntity.ok(movieService.updateMovie(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok("Movie deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}