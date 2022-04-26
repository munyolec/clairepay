package com.clairepay.gateway.Payer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayerService {

    private final PayerRepository payerRepository;
     @Autowired
    public PayerService(PayerRepository payerRepository) {
        this.payerRepository = payerRepository;
    }

    public List<Payer> getAllPayers() {
        return payerRepository.findAll();
    }

}
