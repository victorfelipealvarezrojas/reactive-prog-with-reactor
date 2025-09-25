package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    /**
     * Crea un Flux básico a partir de una lista de nombres
     * fromIterable() - Convierte una colección en un flujo reactivo
     * log() - Registra todos los eventos del flujo para debugging
     */
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .log();
    }

    /**
     * Crea un Mono básico con un único valor
     * just() - Crea un Mono que emite exactamente un elemento
     */
    public Mono<String> namesMonono() {
        return Mono.just("victor").log();
    }

    /**
     * Transforma cada elemento del Flux a mayúsculas
     * map() - Aplica una función de transformación a cada elemento (1 a 1)
     */
    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .map(String::toUpperCase).log();
    }

    /**
     * Filtra elementos según su longitud
     * filter() - Solo deja pasar elementos que cumplan la condición especificada
     */
    public Flux<String> namesFlux_filter(int stringLength) {
        return Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"))
                .filter(s -> s.length() == stringLength);
    }

    /**
     * Demuestra la inmutabilidad de los flujos reactivos
     * Los operadores no modifican el flujo original, crean uno nuevo
     */
    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("victor", "felipe", "joko", "peras"));
        namesFlux.map(String::toUpperCase); // Esta transformación se pierde
        return namesFlux; // Devuelve el flujo original sin modificar
    }

    /**
     * Usa flatMap para dividir cada string en caracteres individuales
     * flatMap() - Transforma cada elemento en un Publisher y aplana el resultado
     */
    public Flux<String> namesFlux_flatmap() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString) // Cada string se convierte en un Flux de caracteres
                .log();
    }

    /**
     * Función auxiliar que divide un string en caracteres
     * fromArray() - Crea un Flux a partir de un array
     */
    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    /**
     * FlatMap con operaciones asíncronas (orden no garantizado)
     * flatMap() - Procesa elementos de forma asíncrona, el orden puede cambiar
     */
    public Flux<String> namesFlux_flatmap_async() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString_async)
                .log();
    }

    /**
     * ConcatMap mantiene el orden secuencial incluso con operaciones asíncronas
     * concatMap() - Como flatMap pero preserva el orden original de los elementos
     */
    public Flux<String> namesFlux_concatmap() {
        return Flux.fromIterable(List.of("vi", "fe"))
                .map(String::toUpperCase)
                .concatMap(this::splitString_async)
                .log();
    }

    /**
     * Función auxiliar que añade delay aleatorio a cada carácter
     * delayElements() - Introduce un retraso entre la emisión de cada elemento
     */
    public Flux<String> splitString_async(String name) {
        var delay = new Random().nextInt(1000);
        var charArray = name.split("");
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    /**
     * Transforma un Mono en una lista usando flatMap
     * flatMap() en Mono - Transforma el valor del Mono en otro Mono
     */
    public Mono<List<String>> names_mono_filter_flatmap(int strLeng) {
        return Mono.just("victor")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLeng)
                .flatMap(this::StringMono);
    }

    /**
     * Convierte un Mono en un Flux usando flatMapMany
     * flatMapMany() - Transforma un Mono en un Flux (de 1 elemento a muchos)
     */
    public Flux<String> names_mono_filter_flatmap_many(int strLeng) {
        return Mono.just("victor")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLeng)
                .flatMapMany(this::StringFlux);
    }

    /**
     * Usa transform para aplicar una función de transformación reutilizable
     * transform() - Aplica una función de transformación al Publisher completo
     */
    public Flux<String> namesFlux_transform() {
        // Function reutilizable que combina map y filter
        Function<Flux<String>, Flux<String>> filterMap = (name) -> name.map(String::toUpperCase)
                .filter(e -> e.length() > 3);

        return Flux.fromIterable(List.of("vi", "fe"))
                .transform(filterMap) // Aplica la función de transformación
                .flatMap(this::splitString)
                .log();
    }

    /**
     * Función auxiliar que convierte string a Flux de caracteres
     */
    private Flux<String> StringFlux(String s) {
        var charArray = s.split("");
        return Flux.fromArray(charArray);
    }

    /**
     * Función auxiliar que convierte string a Mono de lista de caracteres
     */
    private Mono<List<String>> StringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    /**
     * Método principal para ejecutar y probar el código
     * subscribe() - Inicia la ejecución del flujo reactivo y consume los elementos
     */
    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux_flatmap()
                .subscribe(System.out::println); // Consume e imprime cada elemento emitido
    }
}