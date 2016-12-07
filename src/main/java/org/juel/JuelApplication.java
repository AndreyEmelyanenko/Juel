package org.juel;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JuelApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(JuelApplication.class, args);
    }

}
