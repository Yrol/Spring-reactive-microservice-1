package blog.yrol.exception;

import lombok.Getter;
import lombok.Setter;

public class ReviewsClientException extends RuntimeException {

    private String message;
    private Integer statusCode;

    public ReviewsClientException(String message, Integer statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
