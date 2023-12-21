package blog.yrol.client;

import blog.yrol.domain.Movie;
import blog.yrol.domain.MovieInfo;
import blog.yrol.exception.MoviesInfoClientException;
import blog.yrol.exception.MoviesInfoServerException;
import blog.yrol.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Custom WebClient config for consuming moviesInfo service endpoints
 * **/
@Component
@Slf4j
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${rest.client.moviesInfoUrl}")
    private String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var url = moviesInfoUrl.concat("/{id}");



        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()

                // Handling 4xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {

                    var errorReason = clientResponse.statusCode().getReasonPhrase();

                    // Handling 404
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                           String.format("No such movie exist for the ID: %s", movieId), clientResponse.statusCode().value()
                        ));
                    }

                    // Handling default 4xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new MoviesInfoClientException(String.format("Server Exception in MoviesInfoService: %s", errorReason), clientResponse.statusCode().value())))
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                responseMessage, clientResponse.statusCode().value()
                            )));
                }))


                // Handling 5xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {
                    log.info("Movies Info Rest status code: {}", clientResponse.statusCode().value());


                    var errorReason = clientResponse.statusCode().getReasonPhrase();


                    // Handling default 5xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new MoviesInfoServerException(String.format("Server Exception in MoviesInfoService: %s", errorReason))))
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                    String.format("Server Exception in MoviesInfoService: %s", responseMessage)

                            )));

                }))
                .bodyToMono(MovieInfo.class)
                .onErrorMap(WebClientRequestException.class, ex -> new MoviesInfoServerException(String.format("Web Client exception MovieInfoService: %s", ex.getMessage())))
//                .retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }

    /**
     * Retrieving stream of movie info
     * **/
    public Flux<MovieInfo> retrieveMoviesInfoStream() {

        var moviesUrl = moviesInfoUrl.concat("/streams");

        return webClient
                .get()
                .uri(moviesUrl)
                .retrieve()

                // Handling 4xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {

                    log.info("Movies Info Rest status code: {}", clientResponse.statusCode().value());


                    var errorReason = clientResponse.statusCode().getReasonPhrase();

                    // Handling default 4xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new MoviesInfoClientException(String.format("Server Exception in MoviesInfoService: %s", errorReason), clientResponse.statusCode().value())))
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                    responseMessage, clientResponse.statusCode().value()
                            )));
                }))


                // Handling 5xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {
                    log.info("Movies Info Rest status code: {}", clientResponse.statusCode().value());


                    var errorReason = clientResponse.statusCode().getReasonPhrase();


                    // Handling default 5xx errors
                    return clientResponse.bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new MoviesInfoServerException(String.format("Server Exception in MoviesInfoService: %s", errorReason))))
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                    String.format("Server Exception in MoviesInfoService: %s", responseMessage)

                            )));

                }))
                .bodyToFlux(MovieInfo.class)
                .onErrorMap(WebClientRequestException.class, ex -> new MoviesInfoServerException(String.format("Web Client exception MovieInfoService: %s", ex.getMessage())))
//                .retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }
}
