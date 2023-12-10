package blog.yrol.unit;

import blog.yrol.controller.FluxAndMonoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = FluxAndMonoController.class) // Giving access to all the endpoints in FluxAndMonoController
@AutoConfigureWebTestClient // Web test client - Similar to TestRestTemplate in Spring MVC
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;


    /**
     * Flux - Approach 1: Test by verifying the size of the body
     * **/
    @Test
    void flux_approach1() {
        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    /**
     * Flux - Approach 2: testing the actual response body by using the StepVerifier
     * **/
    @Test
    void flux_approach2() {
        var flux = webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(1,2,3)
                .verifyComplete();
    }


    /**
     * Flux - Approach 3: Using lambda and assertions
     * **/
    @Test
    void flux_approach3() {
        var flux = webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> { // using lambda and inside that we do verification

                   var responseBody =  listEntityExchangeResult.getResponseBody();
                   assert Objects.requireNonNull(responseBody).size() == 3;

                   // we could also verify individual elements
                });
    }

    /**
     * Mono - Approach 1: using lambda and assertEquals
     * **/
    @Test
    void mono_approach1() {
        var flux = webTestClient
                .get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> { // using lambda and inside that we do verification

                    var responseBody =  stringEntityExchangeResult.getResponseBody();
                    assertEquals("Hello world", responseBody);

                    // we could also verify individual elements
                });
    }

    /**
     * Stream
     * Using "thenCancel" to make sure streaming will be stopped at a certain point (otherwise it'll continue to run indefinitely)
     * **/
    @Test
    void stream_approach2() {
        var flux = webTestClient
                .get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(0L, 1L,2L,3L)
                .thenCancel();
    }
}
