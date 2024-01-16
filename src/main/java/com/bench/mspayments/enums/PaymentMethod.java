package com.bench.mspayments.enums;

public enum PaymentMethod {

    TRANSFER("TRANSFER"),
    ECHEQ("ECHEQ"),
    DEBIT_CARD("DEBIT_CARD");

    private String paymentMethod;

    PaymentMethod(String paymentMethod) {
        this.setPaymentMethod(paymentMethod);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
