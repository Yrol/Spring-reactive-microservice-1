package blog.yrol.exceptionhandler;

import blog.yrol.exception.ReviewDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * This error handler class will be used for handling errors in customized manner.
 * Ex: when ReviewDataException is occurred, make sure it'll be thrown with status 400 instead of 500 (by default it'll be 500 since it extends RuntimeException).
 * **/
@Component
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception message is {}", ex.getMessage(), ex);
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        var errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());

        // Handling ReviewDataException
        if (ex instanceof ReviewDataException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().writeWith(Mono.just(errorMessage));
        }

        // Handling any other exception
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
