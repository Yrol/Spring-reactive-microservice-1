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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                new Review(null, 1L, "Awesome movie", 9.0),
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
                .uri("/v1/reviews")
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

}
