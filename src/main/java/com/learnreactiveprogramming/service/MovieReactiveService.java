package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        return movieInfoService.movieInfoFlux()
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewMonoCollect = reviewService.retrieveReviewsFlux(movieInfo.getMovieId())
                            .collectList(); // convert collect flux to mono

                    return reviewMonoCollect.map(reviewList -> new Movie(
                                    movieInfo.getMovieId(),
                                    movieInfo,
                                    reviewList
                            )
                    );
                });
    }

    public Mono<Movie> getMovieInfo(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewsFlux, (movieInfo, reviews) -> {
            return new Movie(movieInfo, reviews);
        });
    }

}
