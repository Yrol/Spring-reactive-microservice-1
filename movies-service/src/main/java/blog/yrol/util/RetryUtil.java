package blog.yrol.util;

import blog.yrol.exception.MoviesInfoServerException;
import blog.yrol.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetryUtil {

    public static Retry retrySpec() {
        /**
         * Retry spec that will be used when doing the retry.
         * CConfigured to retry maximum of 3 attempts with a delay of 1 second.
         * Avoiding / exempt 404 errors by filtering only MoviesInfoServerException OR ReviewsServerException
         * Exceptions.propagate will make sure it'll throw the actual exception. Ex: in case of 500, it'll be "Server Exception in MoviesInfoService: %s"
         * If Exceptions.propagate is not used it will throw the default exhaust error which is similar to: "Retries exhausted: 3/3"
         * **/
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));
    }
}
