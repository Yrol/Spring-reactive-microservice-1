package blog.yrol.unit;


import blog.yrol.domain.Review;
import blog.yrol.exceptionhandler.GlobalErrorHandler;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest
// using ContextConfiguration for injecting Router and Handler beans (dependencies) instead of controller (as in MoviesInfoControllerUnitTest) since no controllers are involved
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
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
    void testAddReview_whenInvalidInputProvided_returnBadRequestWithErrors() {

        // Arrange
        var review = new Review(null, null, "Awesome movie", -2.0);

        // Mocking "reviewReactiveRepository::save" of addReview()
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome movie", 9.0)));

        // Assert & Act
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.movieInfoId : must not be null,rating.negative : please pass a non-negative value");
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

    @Test
    void testUpdateMovie_whenInvalidInputDataProvided_returnBadRequestWithErrors() {
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
                .bodyValue(new Review("abc", 1L, updateMovieComment, -5.0))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.negative : please pass a non-negative value");
    }

    @Test
    void testGetReviews_whenMovieInfoIdProvided_returnAllMatchingReviews() {

        var reviews = List.of(
                new Review("abc", 1L, "Awesome movie", 9.0),
                new Review("def", 1L, "Fun to watch", 9.0)
        );

        // Arrange
        when(reviewReactiveRepository.findReviewsByMovieInfoId((Long) any()))
                .thenReturn(Flux.fromIterable(reviews));

        // Act & Assert
        webTestClient
                .get()
                .uri(REVIEWS_URL + "?movieInfoId=1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void testGetReviews_whenRequestingForAllAvailableReviews_returnAllReviews() {
        var reviews = List.of(
                new Review(null, 1L, "Awesome movie", 9.0),
                new Review(null, 1L, "Fun to watch", 9.0)
        );

        // Arrange
        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviews));

        // Act & Assert
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void testDeleteReview_whenReviewIdProvided_deleteAndReturnNoContent() {

        // Arrange

        // Mocking "reviewReactiveRepository::findById" of updateReview()
        when(reviewReactiveRepository.findById((String) any()))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        when(reviewReactiveRepository.deleteById((String) any()))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/abc")
                .exchange()
                .expectStatus().isNoContent();
    }
}
