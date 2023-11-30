package blog.yrol.integration;

import blog.yrol.domain.MovieInfo;
import blog.yrol.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest // Enable to scan repository classes and make them available within test classes
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5") // setting the version to 3.5.5 explicitly due to compatibility issues
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var moviesInfo = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo("abc", "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"))
        );

        /**
         * Save into the DB
         * blockLast - will make sure the movies get saved to DB first before running any test case
         * **/
        movieInfoRepository.saveAll(moviesInfo)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {

        // Arrange

        // Act
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        // Assert
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void findById() {

        // Arrange

        // Act
        var movieInfoMono = movieInfoRepository.findById("abc").log();

        // Assert
        StepVerifier.create(movieInfoMono)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight",movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        // Arrange
        var movieInfoSave = new MovieInfo(null, "Spider-Man: Homecoming", 2005, List.of("Tom Holland"), LocalDate.parse("2017-06-15"));

        // Act
        var movieInfoMono = movieInfoRepository.save(movieInfoSave).log();

        // Assert
        StepVerifier.create(movieInfoMono)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId()); // verifying the persistence by checking the ID (not null)
                    assertEquals("Spider-Man: Homecoming",movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        // Arrange

        /**
         * The block() will give access to the actual type - MovieInfo, instead of Flux or Mono
         * The block() will also block further operations until its action (in this case - findById) is completed
         * **/
        var existingMovie = movieInfoRepository.findById("abc").block(); // the block() will give access to the actual type - MovieInfo, instead of Flux or Mono

        existingMovie.setYear(2023);

        // Act
        var movieInfoMono = movieInfoRepository.save(existingMovie).log();

        // Assert
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(2023, movieInfo.getYear());
                    assertEquals("The Dark Knight",movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void findByYear() {

        // Arrange

        // Act
        var moviesInfoFlux = movieInfoRepository.findByYear(2005).log();

        // Assert
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByName() {
        // Arrange

        // Act
        var moviesInfoFlux = movieInfoRepository.findByName("Batman Begins").log();

        // Assert
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {

        // Arrange

        // Act
        movieInfoRepository.deleteById("abc").block(); // using block to make sure delete happens before any further operation
        var movieInfoFlux = movieInfoRepository.findAll().log();

        // Assert
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }
}