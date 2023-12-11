package blog.yrol.exception;

import lombok.Getter;
import lombok.Setter;

public class ReviewsClientException extends RuntimeException {

    private String message;

    public ReviewsClientException(String message) {
        super(message);
        this.message = message;
    }
}
