package com.example.rabbit;

import com.example.rabbit.lib.Consumer;
import com.example.rabbit.lib.Producer;
import com.example.rabbit.lib.RabbitConfig;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("unused")
@Configuration
public class BeanConfig {

    /**
     * 環境変数
     */
    @Autowired
    private RabbitConfig rabbitConfig;

    /**
     * MQへの非同期送信用テンプレート
     */
    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RestTemplate restTemplate(){return new RestTemplate();}


    /**
     * Producer
     * @return Producer実体
     */
    @Bean
    public Producer producer() {
        return new Producer(rabbitConfig, rabbitTemplate, asyncRabbitTemplate);
    }

    /**
     * Consumer
     * @return Consumer実体
     */
    @Bean
    public Consumer consumer() {
        return new Consumer();
    }

}
