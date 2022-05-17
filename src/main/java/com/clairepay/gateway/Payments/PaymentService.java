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
import com.clairepay.gateway.filter.ThreadLocalRequest;
import com.clairepay.gateway.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final List<String> ALLOWED_CURRENCIES = List.of("KES", "USD", "EUR");
    private final List<String> ALLOWED_COUNTRIES = List.of("KEN", "UGA", "TZA");
    private final PayerRepository payerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentsRepository paymentsRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final RabbitTemplate template;

    @Autowired
    public PaymentService(PayerRepository payerRepository,
                          MerchantRepository merchantRepository,
                          PaymentsRepository paymentsRepository,
                          PaymentMethodRepository paymentMethodRepository,
                          RabbitTemplate template) {
        this.payerRepository = payerRepository;
        this.merchantRepository = merchantRepository;
        this.paymentsRepository = paymentsRepository;
        this.paymentMethodRepository = paymentMethodRepository;
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

   public void createMpesaQueue(String firstName, String lastName, String email, String phoneNumber,
                                      Integer amount){
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
        Expiry expiry = new Expiry(month,year);
        return expiry;
    }

    public Card createCard(int cvv, long cardNumber, Expiry expiry){
        Card card = new Card(cvv, cardNumber, expiry);
        return card;
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


    public void validateCardNumber(int cardNumber){
        if (String.valueOf(cardNumber).length()  < 7 || String.valueOf(cardNumber).length()  > 12) {
            throw new InvalidParameterException("Invalid card number ");
        }
    }

    public PaymentResponse paymentProcessor(PaymentRequest paymentRequest, String apiKey){
    // generate a request_id

    Payments payment = new Payments();
    PaymentResponse response = new PaymentResponse();
    response.setReferenceId(paymentRequest.getReferenceId());
    PayerDTO getPayer = paymentRequest.getPayer();
    Card getCard = paymentRequest.getCard();

    //check that merchant exists
    Optional<Merchant> MerchantOptional = merchantRepository.findByApiKey(apiKey);

    if(MerchantOptional.isPresent()){
        Merchant merchantFound = MerchantOptional.get();
        payment.setMerchant(merchantFound);
        //TODO: read on how to better store this data in db
        merchantFound.setMerchantBalance((merchantFound.getMerchantBalance() + paymentRequest.getAmount()));
    }
    else{
        throw new InvalidParameterException("merchant not found");
    }

    // supported currencies, countries,
    if (!ALLOWED_CURRENCIES.contains(paymentRequest.getCurrency().toUpperCase())) {
        throw new InvalidParameterException("Currency not supported");
    }
    if (!ALLOWED_COUNTRIES.contains(paymentRequest.getCountry().toUpperCase())) {
        throw new InvalidParameterException("Country not supported for the moment");
        }

    //TODO: SOLID - open closed principle

    //check if payment method exists and payment method
    String passedMethod = paymentRequest.getPaymentMethod().getMethodName();

    Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findByMethodNameIgnoreCase(passedMethod);
    if(paymentMethodOptional.isPresent()) {
        payment.setPaymentMethod(paymentMethodOptional.get());
    }
    else{
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
            validateCVV(paymentRequest.getCard().getCvv());
            Expiry newExpiry = createExpiry(year, month);
            Card card = createCard(getCard.getCvv(), getCard.getCardNumber(), newExpiry);
            paymentRequest.setCard(card);
        }

    }

//check if payer exists in database,get email, if new email value create a new payer
    String payerEmail =paymentRequest.getPayer().getEmail();
    Optional<Payer> payerOptional = payerRepository.findByEmail(payerEmail);

    if(payerOptional.isPresent()){
        PayerDTO newPayer = new PayerDTO(payerOptional.get().getFirstName(),payerOptional.get().getLastName(),
                payerOptional.get().getEmail(),payerOptional.get().getPhoneNumber());
        paymentRequest.setPayer(newPayer);
        payment.setPayer(payerOptional.get());
    }
    else{
        //create new payer
        createNewPayer(getPayer.getFirstName(),getPayer.getLastName(),getPayer.getEmail(),getPayer.getPhoneNumber());
        paymentRequest.setPayer(paymentRequest.getPayer());

    }

   //set amount
    // valid amount
    if (paymentRequest.getAmount() < 1) {
        throw new InvalidParameterException("Amount cannot be less than 1");
    }

    if (paymentRequest.getAmount() > 1_000_000) {
        throw new InvalidParameterException("Amount cannot be greater than 1 000 000");
    }
    payment.setAmount(paymentRequest.getAmount());

    //TODO
    // successful payment response code
    if((response.getResponseCode() != "1") && (response.getResponseCode() != "2")
            && (response.getResponseCode() != "4")){
        response.setResponseCode("3");
        response.setResponseDescription("payment successful");
        payment.setTransactionId(response.getTransactionId());
    }
    payment.setTransactionId(response.getTransactionId());

    response.setReferenceId(paymentRequest.getReferenceId());
    payment.setStatus(PaymentsStatus.SUCCESS);

//save payment to DB
    paymentsRepository.save(payment);

   return response;
}

}
