package blog.yrol.client;

import blog.yrol.domain.Review;
import blog.yrol.exception.MoviesInfoClientException;
import blog.yrol.exception.MoviesInfoServerException;
import blog.yrol.exception.ReviewsClientException;
import blog.yrol.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Custom WebClient config for consuming moviesReview service endpoints
 * **/
@Component
@Slf4j
public class ReviewsRestClient {

    private WebClient webClient;

    @Value("${rest.client.reviewsUrl}")
    private String reviewsUrl;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId) {

        /**
         * Constructing the Url using UriComponentsBuilder/
         * Ex: http://localhost:8888/v1/reviews?movieInfoId=2
         * **/
        var url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                // Handling 4xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {

                    var errorReason = clientResponse.statusCode().getReasonPhrase();

                    // Handling 404 (if reviews not found for a given movie then return empty since its valid -  where a movie may not consist of any reviews)
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }

                    // Handling default 4xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new ReviewsClientException(String.format("Server Exception in MoviesReviewService: %s", errorReason), clientResponse.statusCode().value())))
                            .flatMap(responseMessage -> Mono.error(new ReviewsClientException(
                                    responseMessage, clientResponse.statusCode().value())));
                }))

                // Handling 5xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {

                    var errorReason = clientResponse.statusCode().getReasonPhrase();

                    // Handling default 5xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new ReviewsServerException(String.format("Server Exception in MoviesReviewService: %s", errorReason))))
                            .flatMap(responseMessage -> Mono.error(new ReviewsServerException(
                                    String.format("Server Exception in MoviesReviewService: %s", responseMessage)
                            )));
                }))
                .bodyToFlux(Review.class)
                .onErrorMap(WebClientRequestException.class, ex -> new ReviewsServerException(String.format("Web Client exception MoviesReviewService: %s", ex.getMessage())))
                .log();
    }
}
