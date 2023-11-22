package blog.yrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieInfoServiceApplication implements CommandLineRunner {

    public static final Logger LOG = LoggerFactory.getLogger(MovieInfoServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovieInfoServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("MovieInfoServiceApplication started..");
    }
}