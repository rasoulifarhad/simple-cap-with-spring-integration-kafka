package com.farhad.example.cap.simple.config.edge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.MessageChannel;

@Configuration
public class EdgeKafkaConfig {
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

    @Bean("edgeConsumerConfigs")
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "edge_reply" +  groupId);
        return props;
    }


    @Bean("edgeProducerConfigs")
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        return props;
    }


    @Bean("edgeProducerFactory")
    public ProducerFactory<String, String> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());

    }
    @Bean
    public ConsumerFactory<String, String> replyConsumerFactory() {

        return   new DefaultKafkaConsumerFactory<>( consumerConfigs(), 
                                                    () -> new StringDeserializer(),
                                                    () -> new StringDeserializer());
    } 

    @Bean("edgeReplyKafkaTemplate")
    public ReplyingKafkaTemplate<String, String ,String> replyingKafkaTemplate(
                        @Qualifier("edgeProducerFactory") ProducerFactory<String, String> pf,
                        @Qualifier("edgeRepliesContainer") ConcurrentMessageListenerContainer<String, String> repliesContainer) {

        return new ReplyingKafkaTemplate<>(pf, repliesContainer); 
    }

    @Bean("edgeRepliesContainer")
    public ConcurrentMessageListenerContainer<String,String> repliesContainer(
                                ConcurrentKafkaListenerContainerFactory<String, String> factory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer = factory.createContainer(replyTopic);
        repliesContainer.getContainerProperties().setMissingTopicsFatal(false);
        repliesContainer.getContainerProperties().setGroupId("edge_reply" +  groupId);
        repliesContainer.getContainerProperties().setClientId("edge_reply"  + "cap-consumer-client");
        return repliesContainer ;

    }

    public MessageChannel capInput() {

        return MessageChannels.
                        direct()
                        .get();
    }

    @Bean
    public IntegrationFlow capOutboundGateFlow(@Qualifier( "edgeReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template) {

        return IntegrationFlows.from("capInput")
                               .log() 
                               .handle(Kafka
                                         .outboundGateway(template)
                                         .topic(requestTopic)
                                )
                                .get()
                                ;
    }

    // public interface CapFunction extends Function<String,String>  {} 

    // @Bean
    // public IntegrationFlow capOutGate(@Qualifier( "capReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template) {
    //     return IntegrationFlows.from(CapFunction.class,
    //                                         gateway -> gateway.beanName("catOutGate"))
    //                             .handle(Kafka
    //                                 .outboundGateway(template)
    //                                 .topic(capRequestTopic))
    //                             .logAndReply();
    //}

    // @Bean
    // public IntegrationFlow capOutboundGateFlow(@Qualifier( "capReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template,
    //                     @Qualifier("capRequestProducerFactory") ProducerFactory<String, String> requestProducerFactory,
    //                     ConcurrentKafkaListenerContainerFactory<String, String> factory) {

    //     ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(capReplyTopic);
    //     replyContainer.getContainerProperties().setMissingTopicsFatal(false);
    //     replyContainer.getContainerProperties().setGroupId("cap" + groupId);

    //     return IntegrationFlows.from(getReceiveTextChannel())
    //                            .log() 
    //                            .handle(Kafka
    //                                      .outboundGateway(requestProducerFactory,replyContainer)
    //                                      .configureKafkaTemplate(t -> t.defaultReplyTimeout(Duration.ofSeconds(30)))
    //                             )
    //                             .channel("kafkaReplies")
    //                             .logAndReply()
    //                             // .get()
    //                             ;
    // }    

}
