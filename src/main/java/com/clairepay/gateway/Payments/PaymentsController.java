package com.clairepay.gateway.Payments;


import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/clairepay/payments")
public class PaymentsController {

    private final PaymentService service;

    @Autowired
    public PaymentsController(PaymentService service) {
        this.service = service;
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
    public void makePayment(
            @PathVariable("payerId") Long payer,
            @PathVariable("paymentMethodId") Long paymentMethodId,
            @PathVariable("merchantId") Long merchant,
            @PathVariable("amount") Integer amount
    ){
        Payer p = new Payer();
        p.setPayerId(payer);
        PaymentMethod pm = new PaymentMethod();
        pm.setMethodId(paymentMethodId);
        Merchant m = new Merchant();
        m.setMerchantId(merchant);
        Payments payment = new Payments();
        payment.setAmount(String.valueOf(amount));
        service.createPayment(p,m,pm,amount);
    }
}
