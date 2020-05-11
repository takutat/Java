package com.example.rabbit.lib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;

@RequiredArgsConstructor
@Getter
public class Consumer {

    /**
     * ログ出力
     */
    protected final Log logger = LogFactory.getLog(Consumer.class);

    /**
     * 受信
     * @param message MQ経由で通知されたメッセージ
     * @param replyTo MQへの応答用宛先
     * @return 応答メッセージ
     */
    @SuppressWarnings("unused")
    @RabbitListener(queues = "sample-queue", concurrency = "4")
    @SendTo("sample-queue")
    public Message<String> receive(String message, @Header(AmqpHeaders.REPLY_TO) String replyTo) throws Exception {

//        Thread.sleep(300000);
        try {
            logger.info("Response" + message + " 開始");
//            throw new Exception("ほげ");
            var responseMessage = message + ":応答テスト2";

            // 応答返却
            return org.springframework.messaging.support.MessageBuilder
                    .withPayload(responseMessage)
                    .setHeader(AmqpHeaders.REPLY_TO, replyTo)
                    .build();
        }
        finally {
            logger.info("Response" + message + " 終了");
        }

    }

}
