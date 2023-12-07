package blog.yrol.controller;

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

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        return null;
    }

}
