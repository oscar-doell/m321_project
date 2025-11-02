

package com.example.statistikdienst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StatistikdienstApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatistikdienstApplication.class, args);
    }

}
