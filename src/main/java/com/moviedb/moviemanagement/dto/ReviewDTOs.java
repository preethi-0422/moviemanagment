package com.moviedb.moviemanagement.dto;


import jakarta.validation.constraints.*;
import lombok.Data;


public class ReviewDTOs {

    @Data
    public static class ReviewRequest {
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must be at most 10")
        private Integer rating;

        @NotBlank(message = "Comment is required")
        private String comment;
    }

    @Data
    public static class ReviewResponse {
        private Long id;
        private Integer rating;
        private String comment;
        private String username;
        private Long movieId;
        private String movieName;
    }
}
