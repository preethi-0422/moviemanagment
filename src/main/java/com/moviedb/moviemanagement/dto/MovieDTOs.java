package com.moviedb.moviemanagement.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class MovieDTOs {

    @Data
    public static class MovieRequest {
        @NotBlank(message = "Movie name is required")
        private String movieName;

        private Integer duration;

        private List<String> genres;   // was: String genre

        private LocalDate releaseDate;

        private List<String> languages; // was: String language
    }

    @Data
    public static class MovieResponse {
        private Long id;
        private String movieName;
        private Integer duration;
        private List<String> genres;    // was: String genre
        private LocalDate releaseDate;
        private List<String> languages; // was: String language
        private Double averageRating;
        private Integer reviewCount;
    }
}