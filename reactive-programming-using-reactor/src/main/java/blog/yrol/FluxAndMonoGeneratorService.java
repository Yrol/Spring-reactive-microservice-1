package blog.yrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "blog.yrol")
public class FluxAndMonoGeneratorService implements CommandLineRunner {

    public static final Logger LOG = LoggerFactory.getLogger(FluxAndMonoGeneratorService.class);

    @Override
    public void run(String... args) throws Exception {
        LOG.info("FluxAndMonoGeneratorService started..");
    }
}