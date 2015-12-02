package com.payleven.inapp.controller;

import com.payleven.inapp.domain.Payment;
import com.payleven.inapp.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper = new ModelMapper();
    }


    @RequestMapping(value = "/payments", method = RequestMethod.GET)
    public Page<PaymentView> listPayments(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        final Page<Payment> payments = paymentService.list(page, pageSize);

        final List<PaymentView> paymentViews = new ArrayList<>();

        for (Payment payment : payments) {
            final PaymentView view = getPaymentView(payment);
            paymentViews.add(view);
        }

        final Page<PaymentView> response = new PageImpl<>(paymentViews, new PageRequest(page, pageSize), payments.getTotalElements());
        return response;
    }

    @RequestMapping(value = "/charges", method = RequestMethod.POST)
    public PaymentView createCharge(@RequestBody ChargeRequest request) throws Exception {
        final Payment charge = paymentService.createCharge(request.getMerchantToken(), request.getUserToken(), request.getOrderNumber(), request.getAmount(), request.getCurrencyCode());
        return getPaymentView(charge);
    }

    @RequestMapping(value = "/refunds", method = RequestMethod.POST)
    public PaymentView createRefund(@RequestBody RefundRequest request) throws Exception {
        final Payment payment = paymentService.findPaymentByExternalId(request.getOrderNumber());
        final Payment refund = paymentService.createRefund(payment, payment.getExternalId() + "-Refund", request.getAmount(), request.getCurrencyCode());
        return getPaymentView(refund);
    }

    @RequestMapping(value = "/payments/{paymentId}", method = RequestMethod.PUT)
    public PaymentView update(@PathVariable("paymentId") Long paymentId, @RequestBody PaymentView updatedPaymentView) {
        final Payment loadedPayment = paymentService.getPayment(paymentId);
        modelMapper.map(updatedPaymentView, loadedPayment);
        final Payment payment = paymentService.update(loadedPayment);
        return getPaymentView(payment);
    }

    @RequestMapping(value = "/payments/{paymentId}", method = RequestMethod.GET)
    public PaymentView get(@PathVariable("paymentId") Long paymentId) {
        final Payment payment = paymentService.getPayment(paymentId);
        return getPaymentView(payment);
    }

    private PaymentView getPaymentView(Payment payment) {
        final PaymentView view = modelMapper.map(payment, PaymentView.class);
        if (view.getType() == Payment.Type.CHARGE) {
            final List<Payment> refunds = paymentService.getRefunds(view.getId());

            long refundedAmount = 0;

            if (refunds != null) {
                refundedAmount = refunds.stream().filter(r -> Arrays.asList(Payment.Status.OK, Payment.Status.PENDING).contains(r.getStatus())).collect(Collectors.summingLong(Payment::getAmount));
            }
            view.setRefundedAmount(refundedAmount);
        }
        return view;
    }

}
