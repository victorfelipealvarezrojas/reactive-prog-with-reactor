package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@DisplayName("Tests para FluxAndMonoGeneratorService - Programación Reactiva")
@Tag("reactor-core")
public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Nested
    @DisplayName("Tests básicos de Flux y Mono")
    class BasicFluxMonoTests {

        @Test
        @DisplayName("Debería emitir 4 nombres desde un Flux básico")
        @Tag("flux-creation")
        void nameFlux() {
            // Given - Crear el Flux de nombres
            var namesFlux = fluxAndMonoGeneratorService.namesFlux();

            // When & Then - Verificar que emite los 4 nombres en el orden correcto
            // expectNext() - Verifica que los próximos elementos emitidos sean exactamente estos valores
            StepVerifier.create(namesFlux)
                    .expectNext("victor", "felipe", "joko", "peras")
                    .verifyComplete(); // Verifica que el Flux se completa correctamente

            // When & Then - Verificar que emite exactamente 4 elementos
            // expectNextCount() - Verifica que se emitan exactamente N elementos
            StepVerifier.create(namesFlux)
                    .expectNextCount(4)
                    .verifyComplete();

            // When & Then - Verificar combinación: primer elemento específico + contar el resto
            StepVerifier.create(namesFlux)
                    .expectNext("victor")      // Verifica el primer elemento específicamente
                    .expectNextCount(3)        // Cuenta los 3 elementos restantes
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería emitir un solo nombre desde un Mono")
        @Tag("mono-creation")
        void nameMono() {
            // Given - Crear el Mono con un solo nombre
            var namesMono = fluxAndMonoGeneratorService.namesMonono();

            // When & Then - Verificar que emite exactamente "victor"
            StepVerifier.create(namesMono)
                    .expectNext("victor")
                    .verifyComplete();

            // When & Then - Verificar que emite exactamente 1 elemento
            StepVerifier.create(namesMono)
                    .expectNextCount(1)
                    .verifyComplete();

            // When & Then - Verificar elemento específico + contar 0 adicionales
            StepVerifier.create(namesMono)
                    .expectNext("victor")
                    .expectNextCount(0)        // No hay más elementos después del primero
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Tests de transformación con operadores")
    class TransformationOperatorTests {

        @Test
        @DisplayName("Debería convertir todos los nombres a mayúsculas usando map()")
        @Tag("transformation")
        void nameFlux_map() {
            // Given - Flux que transforma nombres a mayúsculas
            Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_map();

            // When & Then - Verificar que todos los nombres están en mayúsculas
            // map() transforma cada elemento aplicando String::toUpperCase
            StepVerifier.create(namesFlux)
                    .expectNext("VICTOR", "FELIPE", "JOKO", "PERAS")
                    .verifyComplete();

            // When & Then - Verificar que sigue siendo 4 elementos después de la transformación
            StepVerifier.create(namesFlux)
                    .expectNextCount(4)
                    .verifyComplete();

            // When & Then - Verificar primer elemento transformado + contar resto
            StepVerifier.create(namesFlux)
                    .expectNext("VICTOR")      // Primer elemento transformado
                    .expectNextCount(3)        // Los 3 restantes también transformados
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería demostrar inmutabilidad - el Flux original no se modifica")
        @Tag("immutability")
        void nameFlux_immutability() {
            // Given - Flux que demuestra inmutabilidad (map() se aplica pero no se guarda)
            Flux<String> namesFluxImm = fluxAndMonoGeneratorService.namesFlux_immutability();

            // When & Then - Los nombres permanecen en minúsculas porque la transformación se perdió
            // Esto demuestra que los operadores de Reactor no mutan el flujo original
            StepVerifier.create(namesFluxImm)
                    .expectNext("victor", "felipe", "joko", "peras") // Sin transformar
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería filtrar nombres que tengan exactamente 4 caracteres")
        @Tag("filtering")
        void nameFlux_filter() {
            // Given - Flux que filtra por longitud de 4 caracteres
            Flux<String> namesFluxFiltered = fluxAndMonoGeneratorService.namesFlux_filter(4);

            // When & Then - Solo "joko" tiene 4 caracteres
            // filter() solo deja pasar elementos que cumplan la condición
            StepVerifier.create(namesFluxFiltered)
                    .expectNext("joko")        // Único nombre con 4 caracteres
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Tests de operadores de aplanamiento (flattening)")
    class FlatteningOperatorTests {

        @Test
        @DisplayName("Debería dividir strings en caracteres usando flatMap() - orden no garantizado")
        @Tag("flattening")
        void namesFlux_flatmap() {
            // Given - Flux que usa flatMap para dividir strings en caracteres
            var names = fluxAndMonoGeneratorService.namesFlux_flatmap();

            // When & Then - flatMap() no garantiza orden, solo verificamos el primer carácter
            // flatMap() transforma cada elemento en un Publisher y los aplana
            StepVerifier.create(names)
                    .expectNext("V")           // Primer carácter de "VI" en mayúsculas
                    .expectNextCount(3)        // Los 3 caracteres restantes: I, F, E
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería manejar operaciones asíncronas con flatMap() - orden no predecible")
        @Tag("async-flattening")
        void namesFlux_flatmap_async() {
            // Given - Flux con operaciones asíncronas usando delays aleatorios
            var names = fluxAndMonoGeneratorService.namesFlux_flatmap_async();

            // When & Then - Con delays aleatorios, solo podemos verificar la cantidad total
            // flatMap() con operaciones asíncronas puede cambiar el orden de emisión
            StepVerifier.create(names)
                    .expectNextCount(4)        // Total de caracteres: V,I,F,E
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería mantener orden secuencial con concatMap() a pesar de operaciones asíncronas")
        @Tag("sequential-flattening")
        void namesFlux_concatmap() {
            // Given - Flux que usa concatMap para mantener orden
            var names = fluxAndMonoGeneratorService.namesFlux_concatmap();

            // When & Then - concatMap() mantiene el orden original incluso con delays
            // concatMap() procesa secuencialmente, esperando que termine cada Publisher
            StepVerifier.create(names)
                    .expectNext("V")           // Primer carácter de "VI"
                    .expectNext("I")           // Segundo carácter de "VI"
                    .expectNext("F")           // Primer carácter de "FE"
                    .expectNext("E")           // Segundo carácter de "FE"
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Tests de conversión Mono a otros tipos")
    class MonoConversionTests {

        @Test
        @DisplayName("Debería convertir Mono en una Lista usando flatMap()")
        @Tag("mono-to-list")
        void names_mono_filter_flatmap() {
            // Given - Mono que se filtra y transforma en lista de caracteres
            var names = fluxAndMonoGeneratorService.names_mono_filter_flatmap(3);

            // When & Then - El resultado es un Mono que contiene una Lista
            // flatMap() en Mono transforma el valor en otro Mono
            StepVerifier.create(names)
                    .expectNext(List.of("V","I","C","T","O","R"))  // Lista completa como un elemento
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debería convertir Mono a Flux de caracteres usando flatMapMany()")
        @Tag("mono-to-flux")
        void names_mono_filter_flatmap_many() {
            // Given - Mono que se convierte en Flux de caracteres individuales
            var names = fluxAndMonoGeneratorService.names_mono_filter_flatmap_many(3);

            // When & Then - El resultado es un Flux que emite cada carácter por separado
            // flatMapMany() convierte un Mono en un Flux (1 elemento → muchos elementos)
            StepVerifier.create(names)
                    .expectNext("V","I","C","T","O","R")  // Cada carácter como elemento separado
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Tests de casos adicionales y edge cases")
    @Tag("edge-cases")
    class AdditionalTests {

        @Test
        @DisplayName("Debería aplicar función de transformación reutilizable con transform()")
        @Tag("transform-operator")
        void namesFlux_transform() {
            // Given - Flux que usa transform() con función reutilizable
            var names = fluxAndMonoGeneratorService.namesFlux_transform();

            // When & Then - transform() aplica una función de transformación al Publisher completo
            // Como "vi" y "fe" tienen ≤3 caracteres, son filtrados, resultado vacío
            StepVerifier.create(names)
                    .verifyComplete();  // No hay elementos porque todos son ≤3 caracteres
        }

        @Test
        @DisplayName("Debería manejar filtrado que no devuelve elementos")
        @Tag("empty-result")
        void nameFlux_filter_no_matches() {
            // Given - Filtrar por longitud que ningún nombre tiene
            Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_filter(10);

            // When & Then - No hay nombres con 10 caracteres
            StepVerifier.create(namesFlux)
                    .verifyComplete();  // Flux vacío pero completado correctamente
        }

        @Test
        void explore_concat() {
            //Given
            var abcdefFlux = fluxAndMonoGeneratorService.explore_concat();

            //When & Then
            StepVerifier.create(abcdefFlux)
                    .expectNext("A","B","C")
                    .expectNext("D","E")
                    .expectNext("F")
                    .verifyComplete();

        }
    }
}