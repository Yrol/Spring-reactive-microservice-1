package blog.yrol.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Router function consist of all the routes of the application
 * **/
@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute() {
        return route()
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello world"))).build();
    }
}
