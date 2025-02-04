package com.example.rabbit.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    protected final Log logger = LogFactory.getLog(Producer.class);

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
                    .sendAndReceive(exchange, routingKey, msg)
                    .completable(); // CompletableFuture に変換。本来ならこれを返却する

            //var result = rabbitFuture.get();
            //var result = rabbitFuture.completable().get();
//            if(result == null) {
//                logger.error(name + "null返却");
//                return null;
//            }
//
//            logger.info(name + result + "返却");
//            return new String(result.getBody(), StandardCharsets.UTF_8);

            return rabbitFuture
                    .thenApply((result) ->{
                        if(result == null) {
                            logger.error(name + "null返却");
                            return null;
                        }

                        logger.info(name + result + "返却");
                        return new String(result.getBody(), StandardCharsets.UTF_8);
                    })
                    .get();
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
