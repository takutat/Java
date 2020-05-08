package com.example.rabbit.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Producer {

    /**
     * ログ出力
     */
    private Logger logger = LoggerFactory.getLogger(Producer.class);

    /**
     * 環境変数
     */
    @NotNull
    private final RabbitConfig rabbitConfig;

    /**
     * MQへの同期送信用テンプレート
     */
    @NotNull
    private final RabbitTemplate rabbitTemplate;

    /**
     * MQへの非同期送信用テンプレート
     */
    @NotNull
    private final AsyncRabbitTemplate asyncRabbitTemplate;

    /**
     * Request（同期）
     * @param name ログに出力する名前
     * @param exchange 送信先のエクスチェンジ
     * @param routingKey 送信先のルーチンキー
     * @param message 送信するメッセージ
     */
    public void sendSyncRequest(String name, String exchange, String routingKey, String message) {

        try {
            logger.info(name + "開始");

            var msg = MessageBuilder
                    .withBody(message.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .setCorrelationId(UUID.randomUUID().toString()) // 冪等ID
                    .build();

            rabbitTemplate.send(exchange, routingKey, msg);
        }
        catch (Exception ex) {
            logger.error(name + "例外", ex);
        }
        finally {
            logger.info(name + "終了");
        }
    }

    /**
     * Request-Reply（同期）
     * @param name ログに出力する名前
     * @param exchange 送信先のエクスチェンジ
     * @param routingKey 送信先のルーチンキー
     * @param message 送信するメッセージ
     */
    public String sendSyncRequestReply(String name, String exchange, String routingKey, String message) {

        try {
            logger.info(name + "開始");

            var msg = MessageBuilder
                    .withBody(message.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .setCorrelationId(UUID.randomUUID().toString()) // 冪等ID
                    .build();

            var result = rabbitTemplate.sendAndReceive(exchange, routingKey, msg);
            if(result == null) {
                logger.error(name + "null返却");
                return null;
            }

            logger.info(name + result + "返却");
            return new String(result.getBody(), StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            logger.error(name + "例外", ex);
            return null;
        }
        finally {
            logger.info(name + "終了");
        }
    }
//
//    /**
//     * Request-Reply（非同期）
//     * @param name ログに出力する名前
//     * @param exchange 送信先のエクスチェンジ
//     * @param routingKey 送信先のルーチンキー
//     * @param message 送信するメッセージ
//     */
//    public String sendAsyncRequestReply(String name, String exchange, String routingKey, String message) {
//
//        try {
//            logger.info(name + "開始");
//
//            var msg = MessageBuilder
//                    .withBody(message.getBytes())
//                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
//                    .setCorrelationId(UUID.randomUUID().toString()) // 冪等ID
//                    .build();
//
//
//            var resultMessage = asyncRabbitTemplate
//                    .sendAndReceive(exchange, routingKey, msg)
//                    .completable()
//                    .thenApply((result) -> new String(result.getBody(), StandardCharsets.UTF_8))
//                    .completeOnTimeout(null, 1000, TimeUnit.MILLISECONDS)
//                    .get();
//
//            if(resultMessage == null) {
//                logger.error(name + "null返却");
//                return null;
//            }
//
//            logger.info(name + resultMessage + "返却");
//            return resultMessage;
//        }
//        catch (Exception ex) {
//            logger.error(name + "例外", ex);
//            return null;
//        }
//        finally {
//            logger.info(name + "終了");
//        }
//    }


    /**
     * Request-Reply（非同期）
     * @param name ログに出力する名前
     * @param exchange 送信先のエクスチェンジ
     * @param routingKey 送信先のルーチンキー
     * @param message 送信するメッセージ
     */
    public String sendAsyncRequestReply(String name, String exchange, String routingKey, String message) {

        try {
            logger.info(name + "開始");

            var msg = MessageBuilder
                    .withBody(message.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .setCorrelationId(UUID.randomUUID().toString()) // 冪等ID
                    .build();

            var rabbitFuture = asyncRabbitTemplate
                    .sendAndReceive(exchange, routingKey, msg);

            var result = rabbitFuture.completable().get();
            if(result == null) {
                logger.error(name + "null返却");
                return null;
            }

            logger.info(name + result + "返却");
            return new String(result.getBody(), StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            logger.error(name + "例外", ex);
            return null;
        }
        finally {
            logger.info(name + "終了");
        }
    }
}
