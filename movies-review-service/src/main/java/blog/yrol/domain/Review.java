package blog.yrol.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document // Mongo DB
public class Review {

    @Id
    private String reviewId;

    @NotNull(message = "rating.movieInfoId : must not be null")
    private Long movieInfoId;

    private String comment;

    @Min(value = 0L, message = "rating.negative : please pass a non-negative value") // rating must be greater than 0
    private Double rating;
}
