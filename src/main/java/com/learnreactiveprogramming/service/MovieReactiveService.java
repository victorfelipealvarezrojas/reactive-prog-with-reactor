package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        return movieInfoService.movieInfoFlux()
                .doOnNext(element -> System.out.println("log-movieInfo::" + element) )
                .doOnSubscribe(subs -> System.out.println("subs is 1::" + subs))
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewMonoCollect = reviewService.retrieveReviewsFlux(movieInfo.getMovieId())
                            .collectList(); // convert collect flux to mono

                    // reviewMonoCollect es un flujo de tipo mono que contiene un array
                    return reviewMonoCollect.map(reviewList -> new Movie(
                                    movieInfo.getMovieId(),
                                    movieInfo,
                                    reviewList
                            )
                    );
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    throw new MovieException(ex);
                })
                .doOnNext(element -> System.out.println("log-movie::" + element))
                .doOnSubscribe(subs -> System.out.println("subs is::" + subs))
                .log();
    }

    public Flux<Movie> getAllMovies_retry() {
        return movieInfoService.movieInfoFlux()
                .doOnNext(element -> System.out.println("log-movieInfo::" + element) )
                .doOnSubscribe(subs -> System.out.println("subs is 1::" + subs))
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewMonoCollect = reviewService.retrieveReviewsFlux(movieInfo.getMovieId())
                            .collectList(); // convert collect flux to mono

                    // reviewMonoCollect es un flujo de tipo mono que contiene un array
                    return reviewMonoCollect.map(reviewList -> new Movie(
                                    movieInfo.getMovieId(),
                                    movieInfo,
                                    reviewList
                            )
                    );
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    return new MovieException(ex);
                })
                .doOnNext(element -> System.out.println("log-movie::" + element))
                .doOnSubscribe(subs -> System.out.println("subs is::" + subs))
                .retry(3)
                .log();
    }

    public Mono<Movie> getMovieInfo(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFluxToMono = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewsFluxToMono, (movieInfo, reviews) -> {
            return new Movie(movieInfo, reviews);
        });
    }

    public Mono<Movie> getMovieInfoException(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFluxToMono = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewsFluxToMono, Movie::new)
                .flatMap(movie -> Mono.error(new RuntimeException("Error Occurred.")));
    }

    public Flux<Movie> getAllMoviesException() {
        return movieInfoService.movieInfoFlux()
                .flatMap(movieInfo -> Flux.error(new RuntimeException("Error Occurred.")));
    }

    public Mono<Movie> getMovieInfoById(long movieId) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(movieId)
                .flatMap(info -> {
                    var reviewsToMono = reviewService.retrieveReviewsFlux(info.getMovieId())
                            .collectList();
                    return reviewsToMono.map(reviews -> new Movie(info.getMovieId(), info, reviews));
                });
    }
}
