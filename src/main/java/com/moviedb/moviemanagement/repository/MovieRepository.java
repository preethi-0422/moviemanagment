package com.moviedb.moviemanagement.repository;


import com.moviedb.moviemanagement.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Search by name
    @Query("SELECT DISTINCT m FROM Movie m WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Movie> findByMovieNameContainingIgnoreCase(@Param("name") String name);

    // Latest movies sorted by releaseDate desc
    @Query("SELECT m FROM Movie m ORDER BY m.releaseDate DESC")
    List<Movie> findLatestMovies();

    // Movies by specific month and year
    @Query("SELECT m FROM Movie m WHERE MONTH(m.releaseDate) = :month AND YEAR(m.releaseDate) = :year ORDER BY m.releaseDate DESC")
    List<Movie> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // ── UPDATED SEARCH QUERY ──
    // Uses DISTINCT + conditional JOIN to handle optional genre and language filters.
    // Each filter is only applied when the parameter is not null/empty.
    @Query("""
        SELECT DISTINCT m FROM Movie m
        LEFT JOIN m.genres g
        LEFT JOIN m.languages l
        WHERE
          (:name IS NULL OR LOWER(m.movieName) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:genre IS NULL OR LOWER(g) = LOWER(:genre))
          AND (:language IS NULL OR LOWER(l) = LOWER(:language))
        """)
    List<Movie> searchMovies(
            @Param("name") String name,
            @Param("genre") String genre,
            @Param("language") String language
    );
}