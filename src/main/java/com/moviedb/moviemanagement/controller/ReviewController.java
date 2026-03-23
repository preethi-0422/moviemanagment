package com.moviedb.moviemanagement.controller;

import com.moviedb.dto.ReviewDTOs.ReviewRequest;
import com.moviedb.dto.ReviewDTOs.ReviewResponse;
import com.moviedb.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getReviewsByMovie(@PathVariable Long movieId) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByMovie(movieId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/movie/{movieId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> addReview(@PathVariable Long movieId,
                                       @Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse response = reviewService.addReview(movieId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}