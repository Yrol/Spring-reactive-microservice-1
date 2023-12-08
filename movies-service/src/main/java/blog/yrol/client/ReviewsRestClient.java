package blog.yrol.client;

import blog.yrol.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;


/**
 * Custom WebClient config for consuming moviesReview service endpoints
 * **/
@Component
public class ReviewsRestClient {

    private WebClient webClient;

    @Value("${rest-client.reviewsUrl}")
    private String reviewsUrl;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId) {

        /**
         * Constructing the Url using UriComponentsBuilder/
         * Ex: http://localhost:8888/v1/reviews/1
         * **/
        var url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Review.class);
    }
}
