package blog.yrol.handler;

import blog.yrol.domain.Review;
import blog.yrol.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }
    public Mono<ServerResponse> addReview(ServerRequest request) {

        /**
         * Getting the POST request as a Mono
         * Accessing the POST request data, convert to a flatmap and save
         * Returning the saved request with HTTP Created status
         * */
        return request.bodyToMono(Review.class)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(savedReview));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var reviewsFlux = reviewReactiveRepository.findAll();
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> putReviews(ServerRequest serverRequest) {

        // Getting the review ID
        var reviewId = serverRequest.pathVariable("id");
        var existingReview  = reviewReactiveRepository.findById(reviewId);

        /**
         * Converting the existing fetched review using flatmap to Review type
         * Map requested values to existing review using map and return
         * Use flatmap again to save the updated review and send response as response with success
         * **/
        return existingReview
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)));
    }
}
