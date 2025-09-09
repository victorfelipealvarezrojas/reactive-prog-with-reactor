package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

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
}
