package com.clairepay.gateway.messaging;


import com.clairepay.gateway.dto.MpesaQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MpesaConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeMessageFromQueue(MpesaQueue mpesaQueue){
        log.info("Message Received from queue : " +mpesaQueue);
    }
}
