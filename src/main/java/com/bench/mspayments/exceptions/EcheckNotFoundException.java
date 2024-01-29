package com.bench.mspayments.exceptions;

public class EcheckNotFoundException extends RuntimeException{

    public EcheckNotFoundException(String message){
        super(message);
    }
}
