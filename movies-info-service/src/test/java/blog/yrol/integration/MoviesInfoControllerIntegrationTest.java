package blog.yrol.integration;

import blog.yrol.domain.MovieInfo;
import blog.yrol.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5") // setting the version to 3.5.5 explicitly due to compatibility issues
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_INFO_URL = "/v1/moviesinfo";

    @BeforeEach
    void setUp() {
        var moviesInfo = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo("abc", "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"))
        );

        /**
         * Save into the DB
         * blockLast - will make sure the movies get saved to DB first before running any test case
         * **/
        movieInfoRepository.saveAll(moviesInfo)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void testAddMovieInfo_whenValidDataIsProvided_returnSuccessWithNewMovie() {

        // Arrange
        var movieInfo = new MovieInfo(null, "Spider-Man: Homecoming", 2005, List.of("Tom Holland"), LocalDate.parse("2017-06-15"));

        // Assert & Act
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
    void testGetAllMovies_whenCallingTheApi_returnAllMovies() {

        // Arrange

        // Assert & Act
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
//                    assert responseBody != null;
//                    assertEquals(2, responseBody.size());
//                });
    }
    
    @Test
    void testGetMovieInfoStream_whenCallingTheApi_streamNewlyCreatedMovies() {

        // Arrange (creating the movie first - publisher)
        var movieInfo = new MovieInfo(null, "Spider-Man: Homecoming", 2005, List.of("Tom Holland"), LocalDate.parse("2017-06-15"));

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

        // Assert & Act (check if the newly created movie is emitted - subscriber)
        var moviesStreamFlux = webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/streams")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        // Verify above movie and then cancel the operation - since streaming is a long-running / continuous operation
        StepVerifier.create(moviesStreamFlux)
                .assertNext(movieInfo1 -> {
                   assert movieInfo1.getMovieInfoId() != null;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testGetMovieById_whenValidIdIsProvided_returnMovie() {

        // Arrange

        // Assert & Act
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
    void testGetMovie_whenAnInvalidIdProvided_returnNotFoundError() {

        // Arrange

        // Assert & Act
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/def")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateMovie_whenValidIdIsProvided_returnUpdatedMovie() {
        // Arrange
        var movieInfo = new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Joseph Gordon-Levitt"), LocalDate.parse("2012-07-18"));

        // Assert & Act
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
                    assertEquals("The Dark Knight Rises", responseBody.getName());
                });
    }

    @Test
    void testUpdateMovie_whenUsingAnInvalidId_returnNotFound() {
        // Arrange
        var movieInfo = new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Joseph Gordon-Levitt"), LocalDate.parse("2012-07-18"));

        // Assert & Act
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/def")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testGetMovies_whenValidYearIsProvided_returnEligibleMovies() {
        // Arrange

        // Assert & Act
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/year/2008")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void testGetMoviesBy_whenAnInvalidYearProvided_returnEmptyList() {
        // Arrange

        // Assert & Act
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/year/2048")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(0);
    }

    @Test
    void testGetMoviesByName_whenValidNameIsProvided_returnEligibleMovies() {
        // Arrange

        // Assert & Act
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/name/Batman Begins")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void testDeleteMovie_whenValidOrInvalidIdIsProvided_returnNoContentResponse() {
        // Arrange

        // Assert & Act
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/abc")
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}