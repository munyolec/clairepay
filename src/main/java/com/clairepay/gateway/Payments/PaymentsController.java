package com.clairepay.gateway.Payments;


import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.dto.PaymentRequest;
import com.clairepay.gateway.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "api/v1/clairepay/payments")
public class PaymentsController {
    private final PaymentService service;
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;

    @Autowired
    public PaymentsController(PaymentService service, PayerRepository payerRepository, MerchantRepository merchantRepository) {
        this.service = service;
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
    }

    @GetMapping("/")
    public List<PaymentsDTO> getAllPayments() {
        return service.getAllPayments();
    }

    @GetMapping(path = "/{payerId}/payments")
    public List<PaymentsDTO> getPayments(@PathVariable("payerId") Long payerId) {
        return service.getPayerPayment(payerId);
    }

    @PutMapping(path = "/makePayment/payer/{payerId}/method/{paymentMethodId}/merchant/{merchantId}/{amount}")
    @ResponseBody
    public void makePayment(
            @PathVariable("payerId") Long payer,
            @PathVariable("paymentMethodId") Long paymentMethodId,
            @PathVariable("merchantId") Long merchant,
            @PathVariable("amount") Integer amount
    ){
        Payer newPayer = new Payer();
        newPayer.setPayerId(payer);

        PaymentMethod newPaymentMethod = new PaymentMethod();
        newPaymentMethod.setMethodId(paymentMethodId);

        Merchant newMerchant = new Merchant();
        newMerchant.setMerchantId(merchant);

        Payments payment = new Payments();
        payment.setAmount(amount);

        service.createPayment(newPayer,newMerchant,newPaymentMethod,amount);
    }

    @PostMapping(value ="postPayment/{paymentMethod}")
    public PaymentResponse processPay(@Valid @RequestBody PaymentRequest paymentRequest,
                                      @PathVariable("paymentMethod") String paymentMethod){
        log.info("Received request::: " + paymentRequest);
        Payments payment = new Payments();
        PaymentMethod newPaymentMethod = new PaymentMethod();

        //check if payer exists in database
        //get email
        //if new email value create a new payer
        String payerEmail =paymentRequest.getPayer().getEmail();

        if(payerRepository.findByEmail(payerEmail).isPresent()){
            paymentRequest.setPayer(payerRepository.findByEmail(payerEmail).get());
        }
        else{
            paymentRequest.setPayer(paymentRequest.getPayer());
        }

        payment.setPayer(paymentRequest.getPayer());

        //check if merchant exists in database
        //get email
        //if new email value create a new merchant

        String merchantEmail = paymentRequest.getMerchant().getEmail();

        if(merchantRepository.findByEmail(merchantEmail).isPresent()){
            paymentRequest.setMerchant(merchantRepository.findByEmail(merchantEmail).get());
        }
        else{
           throw new IllegalStateException("merchant not registered");
        }
        payment.setMerchant(paymentRequest.getMerchant());

        //TODO
        //redo this logic

        if(paymentMethod.equals("card")){
            newPaymentMethod.setMethodId(1L);
            payment.setPaymentMethod(newPaymentMethod);
        }
        else if (paymentMethod.equals("mpesa")){
            newPaymentMethod.setMethodId(2L);
            payment.setPaymentMethod(newPaymentMethod);
        }

        payment.setAmount(paymentRequest.getAmount());
        service.createPayment2(payment);

        PaymentResponse response = new PaymentResponse();
        response.setReferenceId(paymentRequest.getReferenceId());

        return response;
    }


    @GetMapping(path="/paymentFor/{merchantId}")
    public List<PaymentsDTO> getMerchantPayments(
            @PathVariable Long merchantId,
            @RequestHeader("apiKey") String apiKey){
        return service.getMerchantPayments(apiKey);
    }



}
