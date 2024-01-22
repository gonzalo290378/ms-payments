package com.bench.mspayments.exceptions;

public class AccountSenderEqualReceiverException extends RuntimeException{

    public AccountSenderEqualReceiverException(String message){
        super(message);
    }
}
