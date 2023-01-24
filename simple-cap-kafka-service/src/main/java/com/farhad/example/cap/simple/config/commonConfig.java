package com.farhad.example.cap.simple.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class commonConfig {
    

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${kafka.topic.request}")
    private String requestTopic;

    @Value("${kafka.topic.reply}")
    private String replyTopic;
    

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    public NewTopic replyTopic() {
        return TopicBuilder
                .name(replyTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic requestTopic() {
        return TopicBuilder
                .name(requestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
