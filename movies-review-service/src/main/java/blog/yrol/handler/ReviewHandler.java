package blog.yrol.handler;

import blog.yrol.domain.Review;
import blog.yrol.exception.ReviewDataException;
import blog.yrol.exception.ReviewNotFoundException;
import blog.yrol.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    /**
     * Creating Sink for the event publisher
     * latest() - will make sure subscribers get only the latest data, ex: a newly joined subscriber will not see the history
     * **/
    Sinks.Many<Review> reviewsSink = Sinks.many().replay().latest();

    private final ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }
    public Mono<ServerResponse> addReview(ServerRequest request) {

        /**
         * Getting the POST request as a Mono
         * Accessing the POST request data, convert to a flatmap and save
         * Returning the saved request with HTTP Created status
         * doOnNext - a side effect function to inject validation
         * */
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> {
                    reviewsSink.tryEmitNext(review);
                })
                .flatMap(savedReview ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(savedReview));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()) {
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(movieInfoId.get())
                    .switchIfEmpty(Mono.empty());
            return ServerResponse.ok().body(reviewsFlux, Review.class);
        }

        var reviewsFlux = reviewReactiveRepository.findAll();
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> putReviews(ServerRequest serverRequest) {

        // Getting the review ID
        var reviewId = serverRequest.pathVariable("id");
        var existingReview  = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given ID " + reviewId)));

        /**
         * Converting the existing fetched review to Review type using flatmap.
         * Map requested values to existing review using map and then return teh unsaved review.
         * Use flatmap again to save the updated review and send it back with 200 response
         * **/
        return existingReview
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .doOnNext(this::validate)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)));
    }

    public Mono<ServerResponse> deleteReviews(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var existingReview  = reviewReactiveRepository.findById(reviewId);
        return existingReview
                .flatMap(review -> reviewReactiveRepository.deleteById(reviewId)
                        .then(ServerResponse.noContent().build()));
    }


    private void validate(Review review) {

        var constraintViolations = validator.validate(review);
        log.info("constraintViolations : {}", constraintViolations);

        /**
         * validator.validate(review) - will return all error / violations in set
         * Processing the Set - sort and collect all messages that are separated by commas and assign to a string
         * Throw ReviewDataException
         * **/
        if (!constraintViolations.isEmpty()) {
            var errorMessage = constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessage);
        }
    }

    /**
     * A stream endpoint which will subscribe to the review emitted by addReview
     * **/
    public Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewsSink.asFlux(), Review.class)
                .log();
    }
}
