package blog.yrol.controller;

import blog.yrol.domain.MovieInfo;
import blog.yrol.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/moviesinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }

    @GetMapping("/moviesinfo")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMoviesInfo() {
        return movieInfoService.getAllMovies().log();
    }

    @GetMapping("/moviesinfo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> getMovieById(@PathVariable("id") String id) {
        return movieInfoService.getMovieById(id).log();
    }

    @PutMapping("/moviesinfo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> updateMovieById(@RequestBody MovieInfo movieInfo, @PathVariable("id") String id) {
        return movieInfoService.updateMovieInfo(movieInfo, id).log();
    }

    @DeleteMapping("/moviesinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable("id") String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
