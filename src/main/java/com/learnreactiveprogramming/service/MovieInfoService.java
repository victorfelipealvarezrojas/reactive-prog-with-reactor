package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class MovieInfoService {


    // reactive
    public Flux<MovieInfo> movieInfoFlux() {
        var movieInfoList = List.of(
                new MovieInfo(1L, 100L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(2L, 101L, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(3L, 102L, "Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        return Flux.fromIterable(movieInfoList);
    }


    // reactive
    public Mono<MovieInfo> retrieveMovieInfoMonoUsingId(long movieId) {
        var movie = new MovieInfo(movieId, 100L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        return Mono.just(movie);
    }

    // no reactive
    public List<MovieInfo> movieList() {
        delay(1000);
        return List.of(
                new MovieInfo(1L, 100L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(2L, 101L, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(3L, 102L, "Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
    }


    // no reactive
    public MovieInfo retrieveMovieUsingId(long movieId) {
        delay(1000);
        return new MovieInfo(movieId, 100L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
    }
}
