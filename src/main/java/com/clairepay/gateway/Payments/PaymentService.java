package com.clairepay.gateway.Payments;

import com.clairepay.gateway.CardDetails.CardDetails;
import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    /**
     * @return all payments
     */
    public List<PaymentsDTO> getAllPayments() {
        return paymentsRepository.findAll()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * @return paymentsDTO
     */
    private PaymentsDTO convertEntityToDTO(Payments payment) {
        PaymentsDTO paymentsDTO = new PaymentsDTO();
        paymentsDTO.setPaymentId(payment.getPaymentId());
        paymentsDTO.setCurrency(payment.getCurrency());
        paymentsDTO.setAmount(String.valueOf(payment.getAmount()));
        paymentsDTO.setPaymentMethod(payment.getPaymentMethod().getMethodName());
        paymentsDTO.setPayer(payment.getPayer().convertPayerEntityToDTO());

        if((paymentsDTO.getPaymentMethod()).equals("Card")){
            CardDetails card = new CardDetails("391310332842", "07/23");
            paymentsDTO.setCardDetails(card);
        }
        return paymentsDTO;
    }
    /**
     * make a payment
     *
     * @param payer
     * @param merchant
     * @param paymentMethod
     * @param amount
     */


    public void createPayment(Payer payer, Merchant merchant, PaymentMethod paymentMethod, Integer amount){
        Optional<Payer> payingCustomer = payerRepository.findById(payer.getPayerId());
        Optional<Merchant> receivingMerchant = merchantRepository.findById(merchant.getMerchantId());

        Payments payment = new Payments();
        if (payingCustomer.isEmpty()) {
            throw new IllegalArgumentException("payer not found");
        }
        if (receivingMerchant.isEmpty()) {
            throw new IllegalArgumentException("merchant not found");
        }
            payer = payingCustomer.get();
            merchant= receivingMerchant.get();
            payment.setPayer(payer);
            payment.setMerchant(merchant);
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(amount);
            merchant.setMerchantBalance(merchant.getMerchantBalance() + amount);
            payment.setStatus(PaymentsStatus.PENDING);
            paymentsRepository.save(payment);

    }

    /**
     * return payments based on payerID
     */

    public List<PaymentsDTO> getPayerPayment(Long payerId) {
        return paymentsRepository.findByPayerId(payerId)
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    public void createPayment2(Payments payment){
        Integer merchantBalance = payment.getMerchant().getMerchantBalance();
        payment.getMerchant().setMerchantBalance(merchantBalance + payment.getAmount());
        payment.setStatus(PaymentsStatus.PENDING);
        paymentsRepository.save(payment);

    }

    public List<PaymentsDTO> getMerchantPayments(String apiKey) {
        return paymentsRepository.findByPayerApiKey(apiKey)
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }
}
