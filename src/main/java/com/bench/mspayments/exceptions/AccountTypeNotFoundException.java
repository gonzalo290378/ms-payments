package com.bench.mspayments.exceptions;

public class AccountTypeNotFoundException extends RuntimeException{

    public AccountTypeNotFoundException(String message){
        super(message);
    }
}
