package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.dto.*;
import com.clairepay.gateway.error.ApiErrorCode;
import com.clairepay.gateway.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final List<String> ALLOWED_CURRENCIES = List.of("KES", "USD", "EUR");
    private final List<String> ALLOWED_COUNTRIES = List.of("KEN", "UGA", "TZA");
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentsRepository paymentsRepository;
    private final RabbitTemplate template;

    @Autowired
    public PaymentService(PayerRepository payerRepository,
                          MerchantRepository merchantRepository,
                          PaymentsRepository paymentsRepository, RabbitTemplate template) {
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
        this.paymentsRepository = paymentsRepository;
        this.template = template;
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
        return paymentsDTO;
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


    public PaymentResponse paymentProcessor(PaymentRequest paymentRequest, String apiKey){

        Payments payment = new Payments();
        PaymentMethod newPaymentMethod = new PaymentMethod();
        PaymentResponse response = new PaymentResponse();

        response.setReferenceId(paymentRequest.getReferenceId());

        //check if merchant is registered
        if(merchantRepository.findByApiKey(apiKey).isPresent()){
            Merchant merchantFound = merchantRepository.findByApiKey(apiKey).get();
            payment.setMerchant(merchantFound);
            merchantFound.setMerchantBalance((merchantFound.getMerchantBalance() + paymentRequest.getAmount()));
        }
        else{
            response.setResponse_code("1");
            response.setResponse_description("wrong merchant apiKey");
            return(response);
        }

        //set payment method and validate
        if(paymentRequest.getPaymentMethod().equalsIgnoreCase("mpesa")){
            newPaymentMethod.setMethodId(2L);
            payment.setPaymentMethod(newPaymentMethod);

            //creating mpesa queue
            MpesaQueue mpesaQueue = new MpesaQueue(
                    paymentRequest.getPayer().getFirstName(),
                    paymentRequest.getPayer().getLastName(),
                    paymentRequest.getPayer().getPhoneNumber(),
                    paymentRequest.getAmount()
            );
            template.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY,mpesaQueue);


        }else if(paymentRequest.getPaymentMethod().equalsIgnoreCase("card")){
            newPaymentMethod.setMethodId(1L);
            payment.setPaymentMethod(newPaymentMethod);

            //setting card details
            if(paymentRequest.getCard() != null) {
                String cardNumber = paymentRequest.getCard().getCardNumber();
//                String expiry = paymentRequest.getCard().getExpiryDate();

                Integer cvv = paymentRequest.getCard().getCvv();
                Integer year = paymentRequest.getCard().getExpiry().getYear();
                Integer month = paymentRequest.getCard().getExpiry().getMonth();

                Expiry newExpiry = new Expiry(year, month);
                Card card = new Card(cvv, cardNumber, newExpiry);
                paymentRequest.setCard(card);
            }
//            check that card details have been provided
            if(paymentRequest.getCard() == null){
                response.setResponse_code("4");
                response.setResponse_description("card details absent");
                return(response);

            }
        } else {
            response.setResponse_code("2");
            response.setResponse_description("invalid payment method");
        }
//check if payer exists in database,get email, if new email value create a new payer
        String payerEmail =paymentRequest.getPayer().getEmail();

        if(payerRepository.findByEmail(payerEmail).isPresent()){
            paymentRequest.setPayer(payerRepository.findByEmail(payerEmail).get());
        }
        else{
            paymentRequest.setPayer(paymentRequest.getPayer());
        }
        payment.setPayer(paymentRequest.getPayer());

       //set amount
        payment.setAmount(paymentRequest.getAmount());

        //successful payment
        if((response.getResponse_code() != "1") && (response.getResponse_code() != "2")
                && (response.getResponse_code() != "4")){
            response.setResponse_code("3");
            response.setResponse_description("payment successful");
            payment.setTransactionId(response.getTransaction_id());
        }
        payment.setTransactionId(response.getTransaction_id());

        response.setReferenceId(paymentRequest.getReferenceId());
        payment.setStatus(PaymentsStatus.SUCCESS);

    //save payment to DB
        paymentsRepository.save(payment);

       return response;
    }

}
