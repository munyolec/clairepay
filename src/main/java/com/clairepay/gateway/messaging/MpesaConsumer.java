package com.clairepay.gateway.messaging;


import com.clairepay.gateway.dto.MpesaQueue;
import com.clairepay.gateway.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MpesaConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeMessageFromQueue(MpesaQueue mpesaQueue){
        System.out.println("Message Received from queue : " +mpesaQueue);
    }
}
