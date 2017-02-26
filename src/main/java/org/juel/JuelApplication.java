package org.juel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.juel")
public class JuelApplication {

  private static Logger logger = LoggerFactory.getLogger(JuelApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(JuelApplication.class, args);
    }

}
