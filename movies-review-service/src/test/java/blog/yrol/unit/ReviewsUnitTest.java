package blog.yrol.unit;


import blog.yrol.domain.Review;
import blog.yrol.handler.ReviewHandler;
import blog.yrol.repository.ReviewReactiveRepository;
import blog.yrol.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.ArgumentMatchers.any;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class}) // used for injecting required beans (dependencies)
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";

    @Test
    void testAddReview_whenValidInputProvided_createReview() {

        // Arrange
        var review = new Review(null, 1L, "Awesome movie", 9.0);

        // Mocking "reviewReactiveRepository::save" of addReview()
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome movie", 9.0)));

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
    void testUpdateMovie_whenValidInputDataProvided_updateReview() {
        // Arrange
        String updateMovieComment = "Awesome movie update";

        // Mocking "reviewReactiveRepository::findById" of updateReview()
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        // Mocking "reviewReactiveRepository::save" of updateReview()
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, updateMovieComment, 9.0)));

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/abc")
                .bodyValue(new Review("abc", 1L, updateMovieComment, 9.0))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    assertEquals(updateMovieComment, updatedReview.getComment());
                });
    }
}
