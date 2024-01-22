package com.bench.mspayments.enums;

public enum PaymentState {

    IN_PROCESS("IN_PROCESS"),
    APPROVED("APPROVED"),

    REJECTED("REJECTED"),
    DEFERRED_PAYMENT("DEFERRED_PAYMENT");

    private String state;

    PaymentState(String state) {
        this.setState(state);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
