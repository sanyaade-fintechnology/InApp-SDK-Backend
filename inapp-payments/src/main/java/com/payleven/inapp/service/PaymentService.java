package com.payleven.inapp.service;

import com.payleven.inapp.domain.Payment;
import com.payleven.inapp.domain.PaymentRepository;
import com.payleven.inapp.sender.PaylevenRequestSender;
import com.payleven.inapp.sender.PaymentRequest;
import com.payleven.inapp.sender.RefundRequest;
import com.payleven.inapp.sender.ResponseWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaylevenRequestSender requestSender;

    /**
     * Sample method to show creating a charge and sending the corresponding request to payleven.
     *
     * @param merchantToken Coming from the onboarding response.
     * @param userToken     Coming from the user registration (customer app).
     * @param externalId    Coming from the merchant (e.g. order number)
     * @param amount        Value of the amount in minor currency (e.g. Cents).
     * @param currencyCode  Major currency code of ISO 4217 (e.g. EUR).
     * @return created and updated payment
     */
    @Transactional
    public Payment createCharge(String merchantToken, String userToken, String externalId, long amount, String currencyCode) throws Exception {

        Payment charge = createPayment(merchantToken, userToken, externalId, amount, currencyCode, Payment.Type.CHARGE);

        final PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(charge.getAmount());
        paymentRequest.setCurrency(charge.getCurrencyCode());
        paymentRequest.setUseCase("DEFAULT"); // mandatory
        paymentRequest.setUserToken(charge.getPayer());
        paymentRequest.setExternalId(charge.getExternalId());

        final ResponseWrapper validatedResponse = requestSender.send(charge.getMerchant(), paymentRequest);

        if (validatedResponse.isSuccessful() && validatedResponse.isHmacVerified()) {
            charge.setStatus(Payment.Status.PENDING);
        } else {
            LOGGER.warn("Request failed. Response = {}", validatedResponse);
            charge.setStatus(Payment.Status.NOK);
        }

        return paymentRepository.save(charge);
    }

    /**
     * Sample method to show creating a refund and sending the corresponding request to payleven.
     *
     * @param charge        Original charge payment.
     * @param externalId    Coming from the merchant (e.g. refund order number)
     * @param amount        Value of the amount in minor currency (e.g. Cents).
     * @param currencyCode  Major currency code of ISO 4217 (e.g. EUR).
     * @return created and updated refund
     */
    @Transactional
    public Payment createRefund(Payment charge, String externalId, long amount, String currencyCode) throws Exception {

        Payment refund = createPayment(charge.getMerchant(), charge.getPayer(), externalId, amount, currencyCode, Payment.Type.REFUND);

        final RefundRequest refundRequest = new RefundRequest();
        refundRequest.setAmount(refund.getAmount());
        refundRequest.setCurrency(refund.getCurrencyCode());
        refundRequest.setExternalId(refund.getExternalId());

        // Important!! Use charge external id here.
        final ResponseWrapper validatedResponse = requestSender.send(refund.getMerchant(), charge.getExternalId(), refundRequest);

        if (validatedResponse.isSuccessful() && validatedResponse.isHmacVerified()) {
            refund.setStatus(Payment.Status.PENDING);
        } else {
            LOGGER.warn("Request failed. Response = {}", validatedResponse);
            refund.setStatus(Payment.Status.NOK);
        }

        return paymentRepository.save(refund);

    }

    private Payment createPayment(String merchantToken, String userToken, String externalId, long amount, String currencyCode, Payment.Type type) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCurrencyCode(currencyCode);
        payment.setExternalId(externalId);
        payment.setStatus(Payment.Status.NEW);
        payment.setType(type);
        payment.setMerchant(merchantToken);
        payment.setPayer(userToken);
        payment.setCreated(new Date());

        payment = paymentRepository.save(payment);
        return payment;
    }

    @Transactional(readOnly = true)
    public Page<Payment> list(int page, int pageSize) {
        final PageRequest pageRequest = new PageRequest(page, pageSize);
        return paymentRepository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(long paymentId) {
        return paymentRepository.findOne(paymentId);
    }

    @Transactional
    public Payment update(Payment updatedPayment) {
        return paymentRepository.save(updatedPayment);
    }

    @Transactional(readOnly = true)
    public Payment findPaymentByExternalId(String orderNumber) {
        return paymentRepository.findByExternalId(orderNumber);
    }

    public List<Payment> getRefunds(Long id) {
        return paymentRepository.findAllByTypeAndReference(Payment.Type.CHARGE, id);
    }
}
