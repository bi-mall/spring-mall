package com.jason.springbootmall.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  // 交換機名稱：Producer 將訊息發送到此交換機
  public static final String ORDER_EXCHANGE = "order.exchange";
  // Queue 名稱：交換機依照 routing key 將訊息路由到此 Queue
  public static final String ORDER_QUEUE = "order.queue";
  // Routing Key：用來決定訊息要被路由到哪個 Queue
  public static final String ORDER_ROUTING_KEY = "order.routing.key";

  // 應用啟動時自動將 Exchange / Queue / Binding 宣告到 RabbitMQ Server
  @Bean
  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  // Direct Exchange：根據完全符合的 routing key 將訊息路由到對應 Queue
  @Bean
  public DirectExchange orderExchange() {
    return new DirectExchange(ORDER_EXCHANGE);
  }

  // durable=true：RabbitMQ 重啟後 Queue 仍然存在
  @Bean
  public Queue orderQueue() {
    return new Queue(ORDER_QUEUE, true);
  }

  // 將 Queue 綁定到 Exchange，並指定 routing key
  @Bean
  public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
    return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
  }

  // 強制在應用啟動時建立連線並宣告 Exchange / Queue / Binding
  @Bean
  public ApplicationRunner rabbitMQInitializer(RabbitAdmin rabbitAdmin) {
    return args -> rabbitAdmin.initialize();
  }
}
