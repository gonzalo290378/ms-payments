package com.bench.mspayments.exceptions;

public class InsufficientFundsFoundException extends RuntimeException{

    public InsufficientFundsFoundException(String message){
        super(message);
    }
}
