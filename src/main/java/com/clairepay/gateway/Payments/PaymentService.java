package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentService {
    private final PayerRepository payerRepository;
    private final PaymentsRepository paymentsRepository;

    @Autowired
    public PaymentService(PayerRepository payerRepository,
                          PaymentsRepository paymentsRepository) {
        this.payerRepository = payerRepository;
        this.paymentsRepository = paymentsRepository;
    }


    public List<Payments> getAllPayments() {
        return paymentsRepository.findAll();
    }
@Transient
    public void createPayment(Payer payer, Merchant merchant, PaymentMethod paymentMethod, String amount){
        Optional<Payer> payingCustomer = payerRepository.findById(payer.getPayerId());
        Payments payment = new Payments();
        if ((payingCustomer.isPresent()) ==false ) {
            throw new IllegalArgumentException("payer not found");
        }
           String phone = payingCustomer.get().getPhoneNumber();
           payment.setPayer(payer);
           payment.setMerchant(merchant);
           payment.setPaymentMethod(paymentMethod);
           payment.setAmount(amount);
           payment.setStatus(Status.PENDING);


        paymentsRepository.save(payment);

    }
}
