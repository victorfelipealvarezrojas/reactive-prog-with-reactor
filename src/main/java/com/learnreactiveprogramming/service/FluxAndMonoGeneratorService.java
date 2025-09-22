package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .log();
    }

    public Mono<String> namesMonono() {
        return Mono.just("victor").log();
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .map(String::toUpperCase).log();
    }

    public Flux<String> namesFlux_filter(int stringLength) {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .filter(s -> s.length() == stringLength);
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFlux_flatmap() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> namesFlux_flatmap_async() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString_async)
                .log();
    }

    public Flux<String> namesFlux_concatmap() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .concatMap(this::splitString_async)
                .log();
    }

    public Flux<String> splitString_async(String name) {
        var delay = new Random().nextInt(1000);
        var charArray = name.split("");
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    public Mono<List<String>> names_mono_filter_flatmap(int strLeng) {
        return Mono.just("victor")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLeng)
                .flatMap(this::StringMono);
    }

    private Mono<List<String>> StringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux_flatmap()
                .subscribe(System.out::println);
    }
}
