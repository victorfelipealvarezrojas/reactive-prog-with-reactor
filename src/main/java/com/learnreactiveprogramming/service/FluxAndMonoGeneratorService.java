package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .log();
    }
    public Mono<String> namesMonono() {
        return Mono.just("victor").log();
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(System.out::println);

        fluxAndMonoGeneratorService.namesMonono()
                .subscribe(System.out::println);

    }
}
