package blog.yrol.controller;

import blog.yrol.domain.MovieInfo;
import blog.yrol.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/moviesinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
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

    @DeleteMapping("/moviesinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable("id") String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
