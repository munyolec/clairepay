package com.clairepay.gateway.Payer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/clairepay/payers")
public class PayerController {

    private final PayerService payerService;
    @Autowired
    public PayerController(PayerService payerService) {
        this.payerService = payerService;
    }

    @GetMapping
    public List<Payer> getAllPayers() {
        return payerService.getAllPayers();
    }
}
