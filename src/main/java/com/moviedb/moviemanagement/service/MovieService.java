package com.moviedb.moviemanagement.service;


import com.moviedb.moviemanagement.dto.MovieDTOs.*;
import com.moviedb.moviemanagement.entity.Movie;
import com.moviedb.moviemanagement.repository.MovieRepository;
import com.moviedb.moviemanagement.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moviedb.moviemanagement.dto.MovieDTOs.MovieRequest;
import com.moviedb.moviemanagement.dto.MovieDTOs.MovieResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public MovieResponse addMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .movieName(request.getMovieName())
                .duration(request.getDuration())
                .genres(request.getGenres() != null ? request.getGenres() : new ArrayList<>())
                .releaseDate(request.getReleaseDate())
                .languages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>())
                .build();
        return toResponse(movieRepository.save(movie));
    }

    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movie.setMovieName(request.getMovieName());
        movie.setDuration(request.getDuration());
        movie.setGenres(request.getGenres() != null ? request.getGenres() : new ArrayList<>());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>());
        return toResponse(movieRepository.save(movie));
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return toResponse(movie);
    }

    // ── UPDATED: searchMovies now receives genre, language, minRating as dropdowns ──
    public List<MovieResponse> searchMovies(String name, String genre, String language, Double minRating) {

        // Pass null to JPQL when empty string — lets the query skip that filter
        String nameParam     = (name     != null && !name.isBlank())     ? name     : null;
        String genreParam    = (genre    != null && !genre.isBlank())    ? genre    : null;
        String languageParam = (language != null && !language.isBlank()) ? language : null;

        List<Movie> movies = movieRepository.searchMovies(nameParam, genreParam, languageParam);

        // minRating is computed from reviews — filter in Java after DB fetch
        if (minRating != null) {
            movies = movies.stream()
                    .filter(m -> {
                        Double avg = reviewRepository.findAverageRatingByMovieId(m.getId());
                        return avg != null && avg >= minRating;
                    })
                    .collect(Collectors.toList());
        }

        return movies.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MovieResponse> getLatestMovies() {
        return movieRepository.findLatestMovies().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getMoviesByMonthAndYear(int month, int year) {
        return movieRepository.findByMonthAndYear(month, year).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MovieResponse toResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setMovieName(movie.getMovieName());
        response.setDuration(movie.getDuration());
        response.setGenres(movie.getGenres());
        response.setReleaseDate(movie.getReleaseDate());
        response.setLanguages(movie.getLanguages());

        Double avg = reviewRepository.findAverageRatingByMovieId(movie.getId());
        response.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        response.setReviewCount(reviewRepository.findByMovieId(movie.getId()).size());
        return response;
    }
}