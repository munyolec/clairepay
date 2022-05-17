package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Merchant.MerchantRepository;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.PaymentMethod.PaymentMethodRepository;
import com.clairepay.gateway.dto.PayerDTO;
import com.clairepay.gateway.Payer.PayerRepository;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.dto.*;
import com.clairepay.gateway.error.InvalidParameterException;

import com.clairepay.gateway.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.clairepay.gateway.error.ApiErrorCode.PAYMENT_SUCCESSFUL;

@Service
public class PaymentService {
    private final List<String> ALLOWED_CURRENCIES = List.of("KES", "USD", "EUR");
    private final List<String> ALLOWED_COUNTRIES = List.of("KEN", "UGA", "TZA");
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentsRepository paymentsRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final RabbitTemplate template;
    private final ConsumeChargeCardService consumeChargeCardService;

    @Autowired
    public PaymentService(PayerRepository payerRepository,
                          MerchantRepository merchantRepository,
                          PaymentsRepository paymentsRepository,
                          PaymentMethodRepository paymentMethodRepository,
                          RabbitTemplate template, ConsumeChargeCardService consumeChargeCardService) {
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
        this.paymentsRepository = paymentsRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.template = template;
        this.consumeChargeCardService = consumeChargeCardService;
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
    public void createMpesaQueue(String firstName, String lastName, String email, String phoneNumber, Integer amount){
       MpesaQueue mpesaQueue = new MpesaQueue(
               firstName, lastName, email, phoneNumber, amount
       );
       template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, mpesaQueue);
    }
    public void createNewPayer(String firstName, String lastName, String email, String phoneNumber) {
        Payer newPayer= new Payer(firstName,lastName,email,phoneNumber);
        payerRepository.save(newPayer);
    }
    public Expiry createExpiry(int month,int year){
        return new Expiry(month,year);
    }
    public Card createCard(int cvv, long cardNumber, Expiry expiry){
        return new Card(cvv, cardNumber, expiry);
    }
    public void validateExpiry(int year, int month){
        LocalDate now = LocalDate.now();
        if (year < now.getYear()) {
            throw new InvalidParameterException("card has expired");
        }
        if (year == now.getYear() && month < now.getMonth().getValue()) {
            throw new InvalidParameterException("card has expired");
        }
    }
    public void validateCVV(int cvv){
        if (String.valueOf(cvv).length() != 3) {
            throw new InvalidParameterException("Invalid cvv ");
        }
    }
    public void validateCardNumber(long cardNumber){
        if (String.valueOf(cardNumber).length()  < 7 || String.valueOf(cardNumber).length()  > 12) {
            throw new InvalidParameterException("Invalid card number ");
        }
    }
    public void validateAmount(int amount){
        if (amount < 1) {
            throw new InvalidParameterException("Amount cannot be less than 1");
        }
        if (amount > 1_000_000) {
            throw new InvalidParameterException("Amount cannot be greater than 1 000 000");
        }
    }
    public void validateCurrency(String currency){
        if (!ALLOWED_CURRENCIES.contains(currency.toUpperCase())) {
            throw new InvalidParameterException("Currency not supported");
        }
    }
    public void validateCountries(String country){
        if (!ALLOWED_COUNTRIES.contains(country.toUpperCase())) {
            throw new InvalidParameterException("Country not supported");
        }
    }


    //*********================  PAYMENT PROCESSOR ========================************
    public PaymentResponse paymentProcessor(PaymentRequest paymentRequest, String apiKey){
        //TODO: attempt to do db retry
    Payments payment = new Payments(); PaymentResponse response = new PaymentResponse();
    PayerDTO getPayer = paymentRequest.getPayer(); Card getCard = paymentRequest.getCard();
    response.setReferenceId(paymentRequest.getReferenceId());

    //check that merchant exists
    Optional<Merchant> MerchantOptional = merchantRepository.findByApiKey(apiKey);
    if(MerchantOptional.isPresent()){
        Merchant merchantFound = MerchantOptional.get();
        payment.setMerchant(merchantFound);
        //TODO : research on how to better update this data
        merchantFound.setMerchantBalance((merchantFound.getMerchantBalance() + paymentRequest.getAmount()));
    } else {
        throw new InvalidParameterException("merchant not found");
    }

   //validate currency & countries
    validateCurrency(paymentRequest.getCurrency());
    validateCountries(paymentRequest.getCountry());

    //TODO: SOLID - open closed principle
    //check if payment method exists
    String passedMethod = paymentRequest.getPaymentMethod().getMethodName();
    Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findByMethodNameIgnoreCase(passedMethod);
    if(paymentMethodOptional.isPresent()) {
        payment.setPaymentMethod(paymentMethodOptional.get());
    } else {
        throw new InvalidParameterException("payment method not available");
    }

    //creating mpesa queue
    if((paymentRequest.getPaymentMethod().getMethodName().equalsIgnoreCase("MobileMoney"))){
        createMpesaQueue(getPayer.getFirstName(),getPayer.getLastName(), getPayer.getEmail(),
                getPayer.getPhoneNumber(),paymentRequest.getAmount());
    }

    //Require card information for Card Details
    if((paymentRequest.getPaymentMethod().getMethodName().equalsIgnoreCase("card"))) {
        if(paymentRequest.getCard() == null){
            throw new InvalidParameterException("card details required");
        }
        if (paymentRequest.getCard() != null) {
            int month = paymentRequest.getCard().getExpiry().getMonth();
            int year = paymentRequest.getCard().getExpiry().getYear();
            //validate card
            validateCVV(paymentRequest.getCard().getCvv());
            validateCardNumber(paymentRequest.getCard().getCardNumber());
            validateExpiry(year,month);

            Expiry newExpiry = createExpiry(year, month);
            Card card = createCard(getCard.getCvv(), getCard.getCardNumber(), newExpiry);
            paymentRequest.setCard(card);

            ChargeCard chargeCard = new ChargeCard(
                    getPayer.getFirstName(), paymentRequest.getCurrency(), paymentRequest.getCountry(),
                    getPayer.getEmail(), getCard.getCardNumber(),getCard.getCvv(), newExpiry, paymentRequest.getAmount()
                    );

            consumeChargeCardService.callChargeCardAPI(chargeCard);


        }
    }

//check if payer exists in database,get email, if new email value create a new payer
    Optional<Payer> payerOptional = payerRepository.findByEmail(paymentRequest.getPayer().getEmail());
    if(payerOptional.isPresent()){
        PayerDTO newPayer = new PayerDTO(payerOptional.get().getFirstName(),payerOptional.get().getLastName(),
                payerOptional.get().getEmail(),payerOptional.get().getPhoneNumber());
        paymentRequest.setPayer(newPayer);
        payment.setPayer(payerOptional.get());
    } else{
        createNewPayer(getPayer.getFirstName(),getPayer.getLastName(),getPayer.getEmail(),getPayer.getPhoneNumber());
        paymentRequest.setPayer(paymentRequest.getPayer());
    }
   //validate amount and set
    validateAmount(paymentRequest.getAmount());
    payment.setAmount(paymentRequest.getAmount());

    //TODO : successful payment response code
     int code = PAYMENT_SUCCESSFUL.getCode();
     response.setResponseCode(String.valueOf(code));
     response.setResponseDescription("payment successful");

    payment.setTransactionId(response.getTransactionId());
    payment.setStatus(PaymentsStatus.SUCCESS);

    paymentsRepository.save(payment);
   return response;
}

}
