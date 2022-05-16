package com.clairepay.gateway.Payments;


import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.config.RabbitMQConfig;
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
    private final PaymentService service;
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;

    private final RabbitTemplate template;
    @Autowired
    public PaymentsController(PaymentService service,
                              PayerRepository payerRepository,
                              MerchantRepository merchantRepository, RabbitTemplate template) {
        this.service = service;
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
        this.template = template;
    }

    @GetMapping("/")
    public List<PaymentsDTO> getAllPayments() {
        return service.getAllPayments();
    }

    @GetMapping(path = "/{payerId}/payments")
    public List<PaymentsDTO> getPayments(@PathVariable("payerId") Long payerId) {
        return service.getPayerPayment(payerId);
    }

    @PostMapping(value ="postPayment")
    public PaymentResponse processPay(@Valid @RequestBody PaymentRequest paymentRequest,
                                      @RequestHeader("apiKey") String apiKey){

        Payments payment = new Payments();
        PaymentMethod newPaymentMethod = new PaymentMethod();
        PaymentResponse response = new PaymentResponse();
        response.setReferenceId(paymentRequest.getReferenceId());


        //check if merchant is registered
        if(merchantRepository.findByApiKey(apiKey).isPresent()){
            Merchant merchantFound = merchantRepository.findByApiKey(apiKey).get();
            payment.setMerchant(merchantFound);
            merchantFound.setMerchantBalance((merchantFound.getMerchantBalance() + paymentRequest.getAmount()));
        }
        else{
            response.setResponse_code("1");
            response.setResponse_description("wrong merchant apiKey");
            return(response);
        }

        //set payment method and validate
        if(paymentRequest.getPaymentMethod().equalsIgnoreCase("mpesa")){
           newPaymentMethod.setMethodId(2L);
           payment.setPaymentMethod(newPaymentMethod);

            //creating mpesa queue
            MpesaQueue mpesaQueue = new MpesaQueue(
                    paymentRequest.getPayer().getFirstName(),
                    paymentRequest.getPayer().getLastName(),
                    paymentRequest.getPayer().getPhoneNumber(),
                    paymentRequest.getAmount()
            );
//            template.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY,mpesaQueue);
            template.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY,mpesaQueue);


        }else if(paymentRequest.getPaymentMethod().equalsIgnoreCase("card")){
            newPaymentMethod.setMethodId(1L);
            payment.setPaymentMethod(newPaymentMethod);

            //setting card details
            if(paymentRequest.getCard() != null) {
                String cardNumber = paymentRequest.getCard().getCardNumber();
//                String expiry = paymentRequest.getCard().getExpiryDate();

                Integer cvv = paymentRequest.getCard().getCvv();
                Integer year = paymentRequest.getCard().getExpiry().getYear();
                Integer month = paymentRequest.getCard().getExpiry().getMonth();

                Expiry newExpiry = new Expiry(year, month);
                Card card = new Card(cvv, cardNumber, newExpiry);
                paymentRequest.setCard(card);
            }
//            check that card details have been provided
           if(paymentRequest.getCard() == null){
               response.setResponse_code("4");
               response.setResponse_description("card details absent");
               return(response);

           }
        } else {
            response.setResponse_code("2");
            response.setResponse_description("invalid payment method");
        }

        //check if payer exists in database,get email, if new email value create a new payer
        String payerEmail =paymentRequest.getPayer().getEmail();
        if(payerRepository.findByEmail(payerEmail).isPresent() ){
            paymentRequest.setPayer(payerRepository.findByEmail(payerEmail).get());
        }
        else{
            paymentRequest.setPayer(paymentRequest.getPayer());
        }
        payment.setPayer(paymentRequest.getPayer());



        //TODO
        //check for card details if card was passed as payment method


        payment.setAmount(paymentRequest.getAmount());

        if((response.getResponse_code() != "1") && (response.getResponse_code() != "2")
                && (response.getResponse_code() != "4")){
            response.setResponse_code("3");
            response.setResponse_description("payment successful");
            payment.setTransactionId(response.getTransaction_id());
        }
        log.info("Received request::: " + paymentRequest);
        //post payment to database
        service.createPayment2(payment);

        return response;
    }

}
