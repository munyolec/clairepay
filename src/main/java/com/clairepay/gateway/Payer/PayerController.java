package com.clairepay.gateway.Payer;

import com.clairepay.gateway.Payments.Payments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/clairepay/payers")
public class PayerController {

    private final PayerService payerService;
    @Autowired
    public PayerController(PayerService payerService) {
        this.payerService = payerService;
    }

    @GetMapping
    public List<PayerDTO> getAllPayers() {
        return payerService.getAllPayers();
    }

    @GetMapping(path = "/{email}")
    public Boolean getPayer(@PathVariable("email") String email) {
        return payerService.getPayerByEmail(email);
    }

}
