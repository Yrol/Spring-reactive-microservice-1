package blog.yrol.controller;

import blog.yrol.domain.MovieInfo;
import blog.yrol.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;


    /**
     * Creating Sink for the event publisher
     * latest() - will make sure subscribers get only the latest data, ex: a newly joined subscriber will not see the history
     * **/
    Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().latest();

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/moviesinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {

        /**
         * Creating a movie and publishing an event to the Sink - to be consumed by the getMovieInfoStream endpoint
         * **/
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedInfo -> moviesInfoSink.tryEmitNext(savedInfo));
    }

    /**
     * A stream endpoint which will subscribe to the movies emitted by addMovieInfo
     * **/
    @GetMapping(value = "/moviesinfo/streams", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovieInfoStream() {
        return moviesInfoSink.asFlux();
    }

    @GetMapping("/moviesinfo")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMoviesInfo() {
        return movieInfoService.getAllMovies().log();
    }

    /**
     * Get movie info by ID
     * Doesn't rely on a default response, ex; @ResponseStatus(HttpStatus.OK), since the response could vary. Ex: movie not found & etc
     * Mapping getMovieById(MovieInfo) to the ResponseEntity
     * **/
    @GetMapping("/moviesinfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieById(@PathVariable("id") String id) {
        return movieInfoService.getMovieById(id)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))// return not found if
                .log();
    }

    /**
     * Update movie info by ID
     * Doesn't rely on a default response, ex; @ResponseStatus(HttpStatus.OK), since the response could vary. Ex: movie not found & etc
     * Mapping updatedMovieInfo(MovieInfo) to the ResponseEntity
     * **/
    @PutMapping("/moviesinfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieById(@RequestBody MovieInfo updatedMovieInfo, @PathVariable("id") String id) {
        return movieInfoService.updateMovieInfo(updatedMovieInfo, id)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())) // return not found response if update movie is not found in DB
                .log();
    }

    /**
     * Getting all movies by year
     * **/
    @GetMapping("/moviesinfo/year/{year}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getMoviesByYear(@PathVariable("year") Integer year) {
        return movieInfoService.getMoviesByYear(year);
    }

    /**
     * Getting all movies by name
     * */
    @GetMapping("/moviesinfo/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getMoviesByName(@PathVariable("name") String name) {
        return movieInfoService.getMoviesByName(name);
    }

    @DeleteMapping("/moviesinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable("id") String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
