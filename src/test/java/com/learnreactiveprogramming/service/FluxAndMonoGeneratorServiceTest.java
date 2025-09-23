package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void nameFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                .expectNext("victor", "felipe", "joko", "peras")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNextCount(4)
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNext("victor")
                .expectNextCount(3)
                .verifyComplete();


    }

    @Test
    void nameMono() {
        var namesMono= fluxAndMonoGeneratorService.namesMonono();

        StepVerifier.create(namesMono)
                .expectNext("victor")
                .verifyComplete();

        StepVerifier.create(namesMono)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(namesMono)
                .expectNext("victor")
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void nameFlux_map() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_map();

        StepVerifier.create(namesFlux)
                .expectNext("VICTOR", "FELIPE", "JOKO", "PERAS")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNextCount(4)
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNext("VICTOR")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void nameFlux_immutability() {
        Flux<String> namesFluxImm = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFluxImm)
                .expectNext("victor", "felipe", "joko", "peras")
                .verifyComplete();
    }

    @Test
    void nameFlux_filter() {
        Flux<String> namesFluxImm = fluxAndMonoGeneratorService.namesFlux_filter(4);

        StepVerifier.create(namesFluxImm)
                .expectNext("joko")
                .verifyComplete();
    }

    @Test
    @DisplayName("nameFlux_filter")
    void namesFlux_flatmap() {
        var names = fluxAndMonoGeneratorService.namesFlux_flatmap();
        StepVerifier.create(names)
                .expectNext("V")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        var names = fluxAndMonoGeneratorService.namesFlux_flatmap_async();
        StepVerifier.create(names)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        var names = fluxAndMonoGeneratorService.namesFlux_concatmap();
        StepVerifier.create(names)
                .expectNext("V")
                .expectNext("I")
                .expectNext("F")
                .expectNext("E")
                .verifyComplete();
    }

    @Test
    void names_mono_filter_flatmap() {
        var names = fluxAndMonoGeneratorService.names_mono_filter_flatmap(3);
        StepVerifier.create(names)
                .expectNext(List.of("V","I","C","T","O","R"))
                .verifyComplete();
    }

    @Test
    void names_mono_filter_flatmap_many() {
        var names = fluxAndMonoGeneratorService.names_mono_filter_flatmap_many(3);
        StepVerifier.create(names)
                .expectNext("V","I","C","T","O","R")
                .verifyComplete();
    }
}
