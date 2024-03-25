package com.example.wechat.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitConfig {

    @Value("${facility.delete.exchange}")
    private String facilityDeleteExchange;

    @Value("${facility.delete.queue}")
    private String facilityDeleteQueue;

    @Value("${facility.delete.routing-key}")
    private String facilityDeleteRoutingKey;

    @Bean
    public DirectExchange facilityDeleteExchange() {
        return new DirectExchange(facilityDeleteExchange);
    }

    @Bean
    public Queue facilityDeleteQueue() {
        return new Queue(facilityDeleteQueue);
    }

    @Bean
    public Binding facilityDeleteBinding() {
        return BindingBuilder.bind(facilityDeleteQueue())
                .to(facilityDeleteExchange())
                .with(facilityDeleteRoutingKey);
    }
}
