package com.clairepay.gateway.Payer;

import com.clairepay.gateway.Payments.Payments;
import com.clairepay.gateway.Payments.PaymentsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayerService {

    private final PayerRepository payerRepository;
     @Autowired
    public PayerService(PayerRepository payerRepository) {
        this.payerRepository = payerRepository;
    }

    public List<PayerDTO> getAllPayers() {
        return payerRepository.findAll()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }


    private PayerDTO convertEntityToDTO(Payer payer) {
        PayerDTO payerDTO = new PayerDTO();
        payerDTO.setFirstName(payer.getFirstName());
        payerDTO.setLastName(payer.getLastName());
        payerDTO.setPhoneNumber(payer.getPhoneNumber());
        payerDTO.setEmail(payer.getEmail());
        return payerDTO;

    }


    public Boolean getPayerByEmail(String email){
        return payerRepository.selectExistsByEmail(email);

    }

    public List<Payments> getAllPayments() {
         return payerRepository.findAllPayments();
    }

}
