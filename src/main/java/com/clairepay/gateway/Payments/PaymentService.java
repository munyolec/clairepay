package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerDTO;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentsRepository paymentsRepository;

    @Autowired
    public PaymentService(PayerRepository payerRepository,
                          MerchantRepository merchantRepository,
                          PaymentsRepository paymentsRepository) {
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
        this.paymentsRepository = paymentsRepository;
    }


    public List<PaymentsDTO> getAllPayments() {
        return paymentsRepository.findAll()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    private PaymentsDTO convertEntityToDTO(Payments payment) {
        PaymentsDTO paymentsDTO = new PaymentsDTO();
        paymentsDTO.setPaymentId(payment.getPaymentId());
        paymentsDTO.setCurrency(payment.getCurrency());
        paymentsDTO.setAmount(payment.getAmount());
        paymentsDTO.setPaymentMethod(payment.getPaymentMethod().convertPayerMethodEntityToDTO());
        paymentsDTO.setPayer(payment.getPayer().convertPayerEntityToDTO());
        return paymentsDTO;

    }

@Transient
    public void createPayment(Payer payer, Merchant merchant, PaymentMethod paymentMethod, String amount){
        Optional<Payer> payingCustomer = payerRepository.findById(payer.getPayerId());
        Optional<Merchant> receivingMerchant = merchantRepository.findById(merchant.getMerchantId());
        Payments payment = new Payments();
        if ((payingCustomer.isPresent()) ==false ) {
            throw new IllegalArgumentException("payer not found");
        }
        if ((receivingMerchant.isPresent()) ==false ) {
            throw new IllegalArgumentException("merchant not found");
        }
            payer = payingCustomer.get();
            merchant= receivingMerchant.get();
           String phone = payingCustomer.get().getPhoneNumber();
           payment.setPayer(payer);
           payment.setMerchant(merchant);
           payment.setPaymentMethod(paymentMethod);
           payment.setAmount(amount);
           payment.setStatus(Status.PENDING);


        paymentsRepository.save(payment);

    }
}
