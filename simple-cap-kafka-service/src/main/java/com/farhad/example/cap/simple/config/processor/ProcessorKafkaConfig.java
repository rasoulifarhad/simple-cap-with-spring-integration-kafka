package com.farhad.example.cap.simple.config.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ProcessorKafkaConfig {
    

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
 

    @Value("${kafka.request-reply.timeout-ms}")
    private Long replyTimeout;

    @Value("${kafka.topic.request}")
    private String requestTopic;

    @Value("${kafka.topic.reply}")
    private String replyTopic;

    
    @Bean("processorConsumerConfigs")
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"processor_" +  groupId);
        return props;
    }

    @Bean("processorProducerConfigs")
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        return props;
    }

    @Bean("processorReplyProducerFactory")
    public ProducerFactory<String, String> replyProducerFactory() {

        return new DefaultKafkaProducerFactory<>(   producerConfigs());
    }

    @Bean("processorReplyTemplate")
    public KafkaTemplate<String, String> replyTemplate() {
        return new KafkaTemplate<>(replyProducerFactory());
    }

    @Bean("processorConsumerFactory")
    public ConsumerFactory<String, String> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(   consumerConfigs(), 
                                                    () -> new StringDeserializer(),
                                                    () -> new StringDeserializer());
    }

    @Bean("processorKafkaListenerContainerFactory")
    public ConcurrentMessageListenerContainer<String, String> container(
                                        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {

        ConcurrentMessageListenerContainer<String, String> container =
                                    containerFactory.createContainer(requestTopic);

        container.getContainerProperties().setGroupId("processor_" +  groupId);
        container.getContainerProperties().setClientId("processor_"  + "cap-consumer-client");
        container.getContainerProperties().setMissingTopicsFatal(false);

        // container.setAutoStartup(false);
        // container.getContainerProperties().setGroupId(UUID.randomUUID().toString());

        // Properties  props = new Properties();
        // props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // container.getContainerProperties().setKafkaConsumerProperties(props);

        return container;

    }

    @Bean(name = "capChannel")
    public MessageChannel capChannel() {

        return MessageChannels
                    .direct()
                    .get();
    }
    
    @Bean
    public IntegrationFlow capInboundGateFlow(
                @Qualifier("processorKafkaListenerContainerFactory") ConcurrentMessageListenerContainer<String, String> kafkaListenerContainerFactory,
                @Qualifier("processorReplyTemplate") KafkaTemplate<String, String> replyTemplate) {

        return IntegrationFlows
                    .from(
                       Kafka
                        .inboundGateway(kafkaListenerContainerFactory, replyTemplate)
                        .replyTimeout(30_000)       
                    )
                    .channel("capChannel")
                    .handle(String.class,(p, h) ->  StringUtils.capitalize(p) )
                    .get();
    }



}
