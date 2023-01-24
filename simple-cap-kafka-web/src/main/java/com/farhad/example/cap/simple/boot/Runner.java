package com.farhad.example.cap.simple.boot;

import java.time.Duration;

import org.apache.kafka.common.errors.IllegalSaslStateException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class Runner {
  
    

    @Bean
    @Order(1000)
    public CommandLineRunner myRunner(ReplyingKafkaTemplate<String, String, String>    template) {

        return args -> {

            if(!template.waitForAssignment(Duration.ofSeconds(10))) {

                throw new  IllegalSaslStateException("Reply container did not initialize");

            }
                        
        };
    }
}
