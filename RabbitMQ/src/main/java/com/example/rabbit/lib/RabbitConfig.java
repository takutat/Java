package com.example.rabbit.lib;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Data
@Configuration
@EnableRabbit
public class RabbitConfig {

    /**
     * ログ出力
     */
    private Logger logger = LoggerFactory.getLogger(RabbitConfig.class);
    /**
     * ホスト
     */
    @Value("${rabbitConfig.host}")
    private String host = "localhost";

    /**
     * ポート
     */
    @Value("${rabbitConfig.port}")
    private int port = 5672;

    /**
     * ホスト
     */
    @Value("${rabbitConfig.virtualHost}")
    private String virtualHost = "";

    /**
     * ユーザ名
     */
    @Value("${rabbitConfig.user}")
    private String user = "";

    /**
     * パスワード
     */
    @Value("${rabbitConfig.password}")
    private String password = "";

    /**
     * RabbitMQへの接続ファクトリ生成
     * @return RabbitMQへの接続ファクトリ
     */
    @SuppressWarnings("unused")
    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory =new CachingConnectionFactory(this.host, this.port);
        connectionFactory.setUsername(this.user);
        connectionFactory.setPassword(this.password);
//        connectionFactory.setConnectionTimeout();
//        if(!StringUtils.isEmpty(this.virtualHost)) {
//            connectionFactory.setVirtualHost(this.virtualHost);
//        }

        return connectionFactory;
    }


    @SuppressWarnings("unused")
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRetryTemplate(retryTemplate() );
        //template.setRecoveryCallback();

        return new RabbitTemplate(connectionFactory());
    }


    @SuppressWarnings("unused")
    @Bean
    public RetryTemplate retryTemplate() {

        RetryTemplate template = new RetryTemplate();

        // リトライ回数の設定
        template.setRetryPolicy(new SimpleRetryPolicy(5));

        // リトライ間隔の設定
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        template.setBackOffPolicy(fixedBackOffPolicy);
        //template.setListeners();

        return template;
    }

    @SuppressWarnings("unused")
    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate() {
        return new AsyncRabbitTemplate(rabbitTemplate());
        //return new AsyncRabbitTemplate(connectionFactory(), this.exchange, this.routingKey);
    }




    /**
     * リスナの設定
     * @return リスナの設定ファクトリ
     */
    @SuppressWarnings("unused")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        // RabbitMQへの接続ファクトリ設定
        factory.setConnectionFactory(this.connectionFactory());

        // 同時実行するConsumerの数/最大値
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);

        // リトライ情報の設定
        // ３秒間隔で３回リトライする
        factory.setAdviceChain((RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3)
                .backOffOptions(3000, 1.0, 3000)
                .build()
        ));
        factory.setDefaultRequeueRejected(false);

        return factory;
    }


}
