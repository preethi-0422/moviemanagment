package com.moviedb.moviemanagement.service;

import com.moviedb.moviemanagement.dto.ReviewDTOs.*;
import com.moviedb.moviemanagement.entity.Movie;
import com.moviedb.moviemanagement.entity.Review;
import com.moviedb.moviemanagement.entity.User;
import com.moviedb.moviemanagement.repository.MovieRepository;
import com.moviedb.moviemanagement.repository.ReviewRepository;
import com.moviedb.moviemanagement.repository.UserRepository;
import com.moviedb.moviemanagement.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.moviedb.moviemanagement.dto.ReviewDTOs.ReviewRequest;
import com.moviedb.moviemanagement.dto.ReviewDTOs.ReviewResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    public ReviewResponse addReview(Long movieId, ReviewRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .movie(movie)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("Movie not found with id: " + movieId);
        }
        return reviewRepository.findByMovieId(movieId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setUsername(review.getUser().getUsername());
        response.setMovieId(review.getMovie().getId());
        response.setMovieName(review.getMovie().getMovieName());
        return response;
    }
}
