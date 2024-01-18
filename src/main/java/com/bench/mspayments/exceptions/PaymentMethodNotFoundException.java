package com.bench.mspayments.exceptions;

public class PaymentMethodNotFoundException extends RuntimeException{

    public PaymentMethodNotFoundException(String message){
        super(message);
    }
}
