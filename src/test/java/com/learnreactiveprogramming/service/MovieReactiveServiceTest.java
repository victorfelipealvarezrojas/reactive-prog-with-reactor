package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceTest {

    @Mock
    private MovieInfoService movieInfoService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private MovieReactiveService movieReactiveService;

    private Flux<MovieInfo> movieInfoFlux;
    private Flux<Review> reviewsFlux;
    private Mono<MovieInfo> movieInfoMono;
    private Flux<Review> reviewsFluxToMovieInfoMono;

    @BeforeEach
    void setUp() {
        movieInfoFlux = Flux.just(
                new MovieInfo(1L, 100L, "Batman Begins", 2005,
                        List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(2L, 101L, "The Dark Knight", 2008,
                        List.of("Christian Bale", "Heath Ledger"),
                        LocalDate.parse("2008-07-18"))
        );

        movieInfoMono = Mono.just(
                new MovieInfo(1L, 100L, "Batman Begins", 2005,
                        List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15"))
        );

        reviewsFlux = Flux.just(
                new Review(1L, "Great movie!", 8.5),
                new Review(2L, "Excellent!", 9.0),
                new Review(3L, "wow!", 10.0)
        );

        reviewsFluxToMovieInfoMono = Flux.just(
                new Review(1L, "Great movie!", 8.5),
                new Review(1L, "Excellent!", 9.0)
        );
    }

    @Test
    void getAllMovies() {
        // Arrange
        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(reviewsFlux);

        // Act
        var moviesFlux = movieReactiveService.getAllMovies();

        // Assert
        StepVerifier.create(moviesFlux)
                .expectNextMatches(movie -> {
                    return movie.getMovieId().equals(1L) &&
                            movie.getMovie().getName().equals("Batman Begins") &&
                            movie.getReviewList().size() == 3;
                })
                .expectNextMatches(movie -> {
                    return movie.getMovieId().equals(2L) &&
                            movie.getMovie().getName().equals("The Dark Knight") &&
                            movie.getReviewList().size() == 3;
                })
                .verifyComplete();

        // Verify interactions
        verify(movieInfoService).movieInfoFlux();
        verify(reviewService, times(2)).retrieveReviewsFlux(anyLong());
    }

    @Test
    void getAllMovies_noReviews() {
        // Arrange
        var emptyReviewsFlux = Flux.<Review>empty();

        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(emptyReviewsFlux);

        // Act
        var moviesFlux = movieReactiveService.getAllMovies();

        // Assert
        StepVerifier.create(moviesFlux)
                .expectNextMatches(movie -> {
                    return movie.getMovieId().equals(1L) &&
                            movie.getReviewList().isEmpty();
                })
                .expectNextMatches(movie -> {
                    return movie.getMovieId().equals(2L) &&
                            movie.getReviewList().isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getAllMovies_errorInReviewService() {
        // Arrange
        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenReturn(Flux.error(new RuntimeException("Review service error")));

        // Act
        var moviesFlux = movieReactiveService.getAllMovies();

        // Assert
        StepVerifier.create(moviesFlux)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Review service error"))
                .verify();
    }

    @Test
    void getAllMovies_errorInRMovieService() {
        // Arrange
        when(movieInfoService.movieInfoFlux())
                .thenReturn(Flux.error(new RuntimeException("Movie service error")));

        // Act
        var moviesFlux = movieReactiveService.getAllMovies();

        // Assert
        StepVerifier.create(moviesFlux)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Movie service error"))
                .verify();
    }

    @Test
    void getMovieInfo() {
        when(movieInfoService.retrieveMovieInfoMonoUsingId(anyLong())).thenReturn(movieInfoMono);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(reviewsFluxToMovieInfoMono);

        var movieMono = movieReactiveService.getMovieInfo(1L);

        StepVerifier.create(movieMono)
                .expectNextMatches(element -> {
                    return element.getMovieId().equals(1L) &&
                            element.getReviewList().size() == 2;
                })
                .verifyComplete();

    }

    @Test
    void getMovieInfoV2() {
        when(movieInfoService.retrieveMovieInfoMonoUsingId(anyLong())).thenReturn(movieInfoMono);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(reviewsFluxToMovieInfoMono);

        var movieMono = movieReactiveService.getMovieInfo(1L);

        StepVerifier.create(movieMono).expectNextMatches(element -> {
                    assertThat(element.getMovieId()).isNotNull();
                    assertThat(element.getMovieId()).isEqualTo(1L);
                    assertThat(element.getReviewList().size() == 2).isTrue();
                    return true;
                })
                .verifyComplete();

    }

    @Test
    void getMovieInfoById() {
        when(movieInfoService.retrieveMovieInfoMonoUsingId(anyLong())).thenReturn(movieInfoMono);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(reviewsFluxToMovieInfoMono);

        var movieMono = movieReactiveService.getMovieInfoById(1L);

        StepVerifier.create(movieMono).expectNextMatches(element -> {
                    assertThat(element.getMovieId()).isNotNull();
                    assertThat(element.getMovieId()).isEqualTo(1L);
                    assertThat(element.getReviewList().size() == 2).isTrue();
                    return true;
                })
                .verifyComplete();


    }

    @Test
    void getMovieInfoException() {
        when(movieInfoService.retrieveMovieInfoMonoUsingId(anyLong())).thenReturn(movieInfoMono);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenReturn(reviewsFluxToMovieInfoMono);

        var movieMono = movieReactiveService.getMovieInfoException(1L);

        StepVerifier.create(movieMono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getAllMoviesException() {
        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        var movieFlux = movieReactiveService.getAllMoviesException();

        StepVerifier.create(movieFlux)
                .expectNextCount(0)
                .expectErrorMessage("Error Occurred.")
                //.expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void getAllMoviesExceptionHandle() {
        // Arrange
        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("erro"));

        // Act
        var moviesFlux = movieReactiveService.getAllMovies();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();
    }


    @Test
    void getAllMoviesExceptionHandle_retry() {
        // Arrange
        when(movieInfoService.movieInfoFlux()).thenReturn(movieInfoFlux);
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("error"));

        // Act
        var moviesFlux = movieReactiveService.getAllMovies_retry();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }
}