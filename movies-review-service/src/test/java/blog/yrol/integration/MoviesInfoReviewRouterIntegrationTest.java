package blog.yrol.integration;

import blog.yrol.domain.Review;
import blog.yrol.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5") // setting the version to 3.5.5 explicitly due to compatibility issues
@AutoConfigureWebTestClient
public class MoviesInfoReviewRouterIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {

        /**
         * Setting up reviews before each test case
         * Review ID is null, and it will be auto generated when saving
         * **/
        var reviewList = List.of(
                new Review("1", 1L, "Awesome movie", 9.0),
                new Review(null, 1L, "Awesome movie 1", 9.0),
                new Review(null, 2L, "Awesome movie 2", 9.0)
        );

        reviewReactiveRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void testAddReview_whenValidDataIsProvided_returnNewlyCreatedReview() {
        // Arrange
        var review = new Review(null, 1L, "Awesome movie", 9.0);

        // Assert & Act
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                });
    }

    @Test
    void testGetReviews_whenCallingTheApi_returnAllAvailableReviews() {
        // Arrange

        // Assert & act
        webTestClient
                .get()
                .uri(REVIEWS_URL + "?movieInfoId=1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void testReviewsByMovieId_whenValidMovieIdIsProvided_returnAllAvailableReviews() {
        // Arrange

        // Assert & act
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void testUpdateReview_whenProvidingValidInputs_returnUpdatedReview() {

        String updateMovieComment = "Awesome movie update";

        // Arrange
        var review = new Review(null, 1L, updateMovieComment, 9.0);

        // Assert & Act
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/1")
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    assertEquals(updateMovieComment, updatedReview.getComment());
                });
    }

    @Test
    void testDeleteReview_whenValidReviewIdProvided_deleteReviewAndReturnNoContentResponse() {
        // Arrange
        
        // Assert & Act
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
