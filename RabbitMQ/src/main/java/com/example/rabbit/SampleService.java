package com.example.rabbit;

import com.example.rabbit.lib.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SampleService {

    @SuppressWarnings("unused")
    @Autowired
    private Producer producer;

    public String test() throws Exception {

        String routingKey ="AAA";

        // Request（同期）
        {
            String[] exchanges = {
                    "sample-exchange1" // 正常系
                    ,"sample-exchange2" // BindingなしのExchange（構造的な異常ではあるがProducer側あ検知できない）
                    ,"sample-exchange3" // 存在しないExchange
            };

            for(String exchange : exchanges) {

                String name = String.format("Request（同期） %s ", exchange);
                String message = String.format("メッセージ[%s]", exchange);

                producer.sendSyncRequest(name, exchange, routingKey, message);
                Thread.sleep(3000);
            }
        }
//
//
//        // Request-Reply（同期）
//        {
//            String[] exchanges = {
//                    "sample-exchange" // 正常系
//                    ,"sample-exchange2" // BindingなしのExchange（構造的な異常ではあるがProducer側あ検知できない）
//                    ,"sample-exchange3" // 存在しないExchange
//            };
//
//            for(String exchange : exchanges) {
//
//                String name = String.format("Request-Reply（同期） %s ", exchange);
//                String message = String.format("メッセージ[%s]", exchange);
//
//                var result = producer.sendSyncRequestReply(name, exchange, routingKey, message);
//                System.out.println(result);
//                Thread.sleep(3000);
//            }
//        }
//
//
//        // Request-Reply（非同期）
//        {
//            String[] exchanges = {
//                    "sample-exchange" // 正常系
//                    ,"sample-exchange2" // BindingなしのExchange（構造的な異常ではあるがProducer側あ検知できない）
//                    ,"sample-exchange3" // 存在しないExchange
//            };
//
//            for(String exchange : exchanges) {
//
//                String name = String.format("Request-Reply（非同期） %s ", exchange);
//                String message = String.format("メッセージ[%s]", exchange);
//
//                var result = producer.sendAsyncRequestReply(name, exchange, routingKey, message);
//                System.out.println(result);
//                Thread.sleep(3000);
//            }
//        }

        return "せいこう";
    }
}
