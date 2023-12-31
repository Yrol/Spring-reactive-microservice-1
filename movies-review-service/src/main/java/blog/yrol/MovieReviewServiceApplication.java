package blog.yrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@ComponentScan(basePackages = "blog.yrol")
public class MovieReviewServiceApplication implements CommandLineRunner {
    public static final Logger LOG = LoggerFactory.getLogger(MovieReviewServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovieReviewServiceApplication.class, args);;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("MovieReviewServiceApplication started..");
    }
}