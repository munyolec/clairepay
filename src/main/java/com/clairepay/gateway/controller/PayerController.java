package com.clairepay.gateway.controller;

import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.service.PayerService;
import com.clairepay.gateway.dto.PayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @GetMapping(value ="/")
    public List<PayerDTO> getAllPayers() {
        return payerService.getAllPayers();
    }

    @GetMapping(path = "/{email}")
    public Optional<Payer> getPayer(@PathVariable("email") String email) {
        return payerService.getPayerByEmail(email);
    }

    @PostMapping("/newPayer")
    @ResponseBody
    public Payer addNewPayer(@Valid @RequestBody Payer payer) {
         return this.payerService.addNewPayer(payer);

    }
}
