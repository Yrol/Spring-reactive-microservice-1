package blog.yrol.controller;

import blog.yrol.client.MoviesInfoRestClient;
import blog.yrol.client.ReviewsRestClient;
import blog.yrol.domain.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    /**
     * Using Non-blocking spring boot webclient
     * This webclient will be used for calling external services in a non-blocking fashion
     * https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html
     * **/
    @Autowired
    private WebClient webClient;

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {

        /**
         * Fetch movies and reviews
         * Calling moviesInfoRestClient.retrieveMovieInfo and reviewsRestClient.retrieveReviews (only if retrieveMovieInfo exist) in order
         * Using flatMap to convert reactive type Mono returned by retrieveMovieInfo.
         * Using collectList to convert reactive type Flux to a List (since Movie -> reviewList is a type List)
         * **/
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                  var reviewListMono = reviewsRestClient.retrieveReviews(movieId)
                          .collectList();

                    return reviewListMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }

}
