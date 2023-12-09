package blog.yrol.client;

import blog.yrol.domain.MovieInfo;
import blog.yrol.exception.MoviesInfoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {

                    // Handling 404
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                           "No such movie exist for the ID: " + movieId, clientResponse.statusCode().value()
                        ));
                    }

                    // Handling default 4xx errors
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                responseMessage, clientResponse.statusCode().value()
                            )));
                })

                // Handling 5xx errors (only if returned / emitted by the moviesInfo service)
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {

                    // Handling default 4xx errors
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                    "Server Exception in MoviesInfoService: " + responseMessage, clientResponse.statusCode().value()
                            )));
                })
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
