package blog.yrol.integration;


import blog.yrol.domain.Movie;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 8084) // spin up a http test server on port 8084 to run tests

// Using TestPropertySource to override the actual service endpoints (moviesinfo and reviews) with port 8084 to communicate with the wiremock server (create in above AutoConfigureWireMock)
@TestPropertySource(
        properties = {
                "rest.client.moviesInfoUrl=http://localhost:8084/v1/moviesinfo",
                "rest.client.reviewsUrl=http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void retrieveMovieById() {

        var movieId = "abc";

        // Creating GET stub for fetching a movie by Id (in MoviesInfoRestClient)
        stubFor(get(urlEqualTo("/v1/moviesinfo/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesinfo.json")));


        stubFor(get(urlEqualTo("/v1/reviews?movieInfoId=" + movieId))
//                .withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });
    }
}
