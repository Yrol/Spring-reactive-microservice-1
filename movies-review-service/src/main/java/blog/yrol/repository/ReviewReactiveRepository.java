package blog.yrol.repository;

import blog.yrol.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    // Using the automatic query builder for getting all reviews by movie ID
    Flux<Review> findReviewsByMovieInfoId(String movieInfoId);
}
