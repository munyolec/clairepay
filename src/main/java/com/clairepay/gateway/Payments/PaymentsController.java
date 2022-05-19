package com.clairepay.gateway.Payments;


import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "api/v1/clairepay/payments")
public class PaymentsController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentsController(PaymentService service) {
        this.paymentService = service;
    }

    @GetMapping("/")
    public List<PaymentsDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping(path = "/{payerId}/payments")
    public List<PaymentsDTO> getPayments(@PathVariable("payerId") Long payerId) {
        return paymentService.getPayerPayment(payerId);
    }


    @PostMapping(value = "postPayment")
    public PaymentResponse processPay2(@Valid @RequestBody PaymentRequest paymentRequest,
                                       @RequestHeader("apiKey") String apiKey) {

        log.info("Request received -->" + paymentRequest);
        PaymentResponse response = paymentService.paymentProcessor(paymentRequest, apiKey);
        log.info("API Response  -->" + response);
        return response;
    }
}
