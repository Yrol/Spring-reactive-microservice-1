package blog.yrol.unit;

import blog.yrol.controller.MoviesInfoController;
import blog.yrol.domain.MovieInfo;
import blog.yrol.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoServiceMock;

    static String MOVIES_INFO_URL = "/v1/moviesinfo";


    @Test
    void getAllMoviesInfo() {

        var moviesInfo = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo("abc", "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"))
        );

        when(movieInfoServiceMock.getAllMovies()).thenReturn(Flux.fromIterable(moviesInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }



    @Test
    void getMovieById() {
        var movieInfo = new MovieInfo("abc", "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));

        when(movieInfoServiceMock.getMovieById("abc")).thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/abc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertEquals("The Dark Knight", responseBody.getName());
                });
    }

    @Test
    void testCreateMovie_whenValidDataIsProvided_createAndReturnMovie() {
        var movieInfo = new MovieInfo(UUID.randomUUID().toString(), "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));

        when(movieInfoServiceMock.addMovieInfo(any(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertNotNull(responseBody.getMovieInfoId());
                });
    }

    @Test
    void testCreateMovie_whenInvalidDataIsProvided_returnValidationError() {
        var movieInfo = new MovieInfo(UUID.randomUUID().toString(), "", -2008, List.of(""), LocalDate.parse("2008-07-18"));

        // Not required since it won't reach the service layer as the validation error will be thrown at controller level
//        when(movieInfoServiceMock.addMovieInfo(any(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                   var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println(responseBody);

                    // Asserting against the validation errors thrown by GlobalExceptionHandler -> handleRequestBodyException
                    assertEquals("movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a positive value", responseBody);
                });
    }

    @Test
    void updateMovie() {
        var movieInfo = new MovieInfo("abc", "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));

        when(movieInfoServiceMock.updateMovieInfo(any(MovieInfo.class), anyString())).thenReturn(Mono.just(movieInfo));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/abc")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertEquals("The Dark Knight", responseBody.getName());
                });
    }

    @Test
    void deleteMovie() {
        when(movieInfoServiceMock.deleteMovieInfo(anyString())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/abc")
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
