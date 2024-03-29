package com.clairepay.gateway.service;

import com.clairepay.gateway.models.Merchant;
import com.clairepay.gateway.models.Payments;
import com.clairepay.gateway.models.PaymentsStatus;
import com.clairepay.gateway.repository.MerchantRepository;
import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.repository.PaymentMethodRepository;
import com.clairepay.gateway.repository.PaymentsRepository;
import com.clairepay.gateway.dto.PayerDTO;
import com.clairepay.gateway.repository.PayerRepository;
import com.clairepay.gateway.models.PaymentMethod;
import com.clairepay.gateway.dto.*;
import com.clairepay.gateway.error.ApiErrorCode;
import com.clairepay.gateway.error.InvalidParameterException;

import com.clairepay.gateway.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void publishToMpesaQueue(String firstName, String lastName, String email, String phoneNumber, Integer amount) {
        MpesaQueue mpesaQueue = new MpesaQueue(
                firstName, lastName, email, phoneNumber, amount
        );
        template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, mpesaQueue);
    }

    public Payer createNewPayer(String firstName, String lastName, String email, String phoneNumber) {
        Payer newPayer = new Payer(firstName, lastName, email, phoneNumber);
        payerRepository.save(newPayer);
        return newPayer;
    }

    public Expiry createExpiry(int month, int year) {
        return new Expiry(month, year);
    }

    public Card createCard(int cvv, long cardNumber, Expiry expiry) {
        return new Card(cvv, cardNumber, expiry);
    }

    public void validateExpiry(int year, int month) {
        LocalDate now = LocalDate.now();
        if (year < now.getYear()) {
            throw new InvalidParameterException("card has expired");
        }
        if (year == now.getYear() && month < now.getMonth().getValue()) {
            throw new InvalidParameterException("card has expired");
        }
    }

    public void validateYear(int year) {
        if (String.valueOf(year).length() != 4) {
            throw new InvalidParameterException("invalid year");
        }
    }

    public void validateCVV(int cvv) {
        if (String.valueOf(cvv).length() != 3) {
            throw new InvalidParameterException("Invalid cvv");
        }
    }

    public void validateCardNumber(long cardNumber) {
        if (String.valueOf(cardNumber).length() < 7 || String.valueOf(cardNumber).length() > 12) {
            throw new InvalidParameterException("Invalid card number");
        }
    }

    public void validateAmount(int amount) {
        if ((amount < 1)) {
            throw new InvalidParameterException("Amount cannot be less than 1");
        }
        if (amount > 1_000_000) {
            throw new InvalidParameterException("Amount cannot be greater than 1 000 000");
        }
    }

    public void validateCurrency(String currency) {
        if (!ALLOWED_CURRENCIES.contains(currency.toUpperCase())) {
            throw new InvalidParameterException("Currency not supported");
        }
    }

    public void validateCountries(String country) {
        if (!ALLOWED_COUNTRIES.contains(country.toUpperCase())) {
            throw new InvalidParameterException("Country not supported");
        }
    }

    public void validateReferenceId(String referenceId) {
        Optional<Payments> paymentOptional = paymentsRepository.findByReferenceId(referenceId);
        if (paymentOptional.isPresent()) {
            throw new InvalidParameterException("duplicate reference id");
        }
    }

    public Merchant validateMerchant(String apiKey1) {
        Optional<Merchant> MerchantOptional = merchantRepository.findByApiKey(apiKey1);
        if (MerchantOptional.isPresent()) {
            return MerchantOptional.get();
        } else {
            throw new InvalidParameterException("merchant not found");
        }
    }

    public Payer validatePayer(String email){
        Optional<Payer> payerOptional = payerRepository.findByEmail(email);
        return payerOptional.orElse(null);
    }
    public PaymentMethod validatePaymentMethod(String passedMethod) {
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findByMethodNameIgnoreCase(passedMethod);
        if (paymentMethodOptional.isPresent()) {
            return paymentMethodOptional.get();
        } else {
            throw new InvalidParameterException("payment method not available");
        }
    }

    public Card validateCard(Card card) {
        validateCVV(card.getCvv());
        validateCardNumber(card.getCardNumber());
        validateYear(card.getExpiry().getYear());
        validateExpiry(card.getExpiry().getYear(), card.getExpiry().getMonth());
        return card;
    }





    //*********================  PAYMENT PROCESSOR ========================************
    @Transactional
    public PaymentResponse paymentProcessor(PaymentRequest paymentRequest, String apiKey) {
        //TODO: attempt to do db retry
        Payments payment = new Payments();
        PaymentResponse response = new PaymentResponse();
        PayerDTO getPayer = paymentRequest.getPayer();
        Card getCard = paymentRequest.getCard();
        response.setReferenceId(paymentRequest.getReferenceId());

        //check that merchant exists
        Merchant merchantFound = validateMerchant(apiKey);
        payment.setMerchant(merchantFound);
        //TODO : research on how to better update this data
        merchantFound.setMerchantBalance((merchantFound.getMerchantBalance() + paymentRequest.getAmount()));

        //validate currency & countries
        validateCurrency(paymentRequest.getCurrency());
        validateCountries(paymentRequest.getCountry());

        //TODO: SOLID - open closed principle
        //check if payment method exists
        if (paymentRequest.getPaymentMethod() == null) {
            throw new InvalidParameterException("payment method is required");
        }

        //validate payment method and set
        PaymentMethod methodExists = validatePaymentMethod(paymentRequest.getPaymentMethod().getMethodName());
        payment.setPaymentMethod(methodExists);

        //publishing to mpesa queue
        if ((paymentRequest.getPaymentMethod().getMethodName().equalsIgnoreCase("MobileMoney"))) {
            publishToMpesaQueue(getPayer.getFirstName(), getPayer.getLastName(), getPayer.getEmail(),
                    getPayer.getPhoneNumber(), paymentRequest.getAmount());
        }

        //Require card information for Card Details and call charge card API
        if ((paymentRequest.getPaymentMethod().getMethodName().equalsIgnoreCase("card"))) {
            if (paymentRequest.getCard() == null) {
                throw new InvalidParameterException("card details required");
            }
            if (paymentRequest.getCard() != null) {
                int month = paymentRequest.getCard().getExpiry().getMonth();
                int year = paymentRequest.getCard().getExpiry().getYear();
                Expiry newExpiry = createExpiry(year, month);
                Card card = createCard(getCard.getCvv(), getCard.getCardNumber(), newExpiry);
                Card validatedCard = validateCard(card);
                paymentRequest.setCard(validatedCard);

                //call card API
                ChargeCard chargeCard = new ChargeCard(
                        getPayer.getFirstName(), paymentRequest.getCurrency(), paymentRequest.getCountry(),
                        getPayer.getEmail(), getCard.getCardNumber(), getCard.getCvv(), newExpiry, paymentRequest.getAmount()
                );
                consumeChargeCardService.callChargeCardAPI(chargeCard);
            }
        }
        //check if whether payer exists
        Payer payerExists = validatePayer (paymentRequest.getPayer().getEmail());
        if(payerExists!=null){
            paymentRequest.setPayer(payerExists.convertPayerEntityToDTO());
            payment.setPayer(payerExists);
        }
        else{
            Payer payerNew = createNewPayer(getPayer.getFirstName(), getPayer.getLastName(), getPayer.getEmail(),
                    getPayer.getPhoneNumber());
            paymentRequest.setPayer(paymentRequest.getPayer());
            payment.setPayer(payerNew);
        }

        //validate amount and set
        validateAmount(paymentRequest.getAmount());
        payment.setAmount(paymentRequest.getAmount());

        int code = PAYMENT_SUCCESSFUL.getCode();
        response.setResponseCode(String.valueOf(code));
        response.setResponseDescription(ApiErrorCode.getDescription(code));

        validateReferenceId(response.getReferenceId());
        payment.setReferenceId(response.getReferenceId());

        payment.setTransactionId(response.getTransactionId());
        payment.setStatus(PaymentsStatus.SUCCESS);

        paymentsRepository.save(payment);

        return response;
    }

}
