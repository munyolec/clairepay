package com.clairepay.gateway.dto;


import com.clairepay.gateway.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MpesaConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeMessageFromQueue(MpesaQueue mpesaQueue){
        System.out.println("Message Received from queue : " +mpesaQueue);
    }
}
