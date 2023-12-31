package blog.yrol.router;

import blog.yrol.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Router function consist of all the routes of the application
 * **/
@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewsHandler) {

        /**
         * Using nested endpoints since "/v1/reviews" is common
         * **/
        return route()
                .nest(path("/v1/reviews"), builder ->
                        builder
                                .GET("", reviewsHandler::getReviews)
                                .POST("", reviewsHandler::addReview)
                                .PUT("/{id}", reviewsHandler::putReviews)
                                .DELETE("/{id}", reviewsHandler::deleteReviews)
                                .GET("/streams", reviewsHandler::getReviewsStream))
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("HelloWorld")))
                .build();
    }
}
