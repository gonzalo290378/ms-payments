package com.bench.mspayments.validator;

import java.time.LocalDate;

public class Validator {

    public Boolean isAccountAndTypeValid(Long accountSender, Long accountReceiver) {
        return true;
    }

    public Boolean isIssueAndPaymentDateValid(LocalDate issueDate, LocalDate paymentDate) {
        return true;
    }

    public Boolean isAmountValid(Double amount) {
        return true;
    }

}
