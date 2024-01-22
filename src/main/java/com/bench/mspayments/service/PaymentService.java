package com.bench.mspayments.service;

import com.bench.mspayments.dto.*;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import com.bench.mspayments.model.EMoney;
import java.util.Optional;


public interface PaymentService {
    public PaymentDTO filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO);
    public Optional<Account> getAccount(Long accountSender);
    public BankTransfer saveBankTransfer(BankTransferResponseDTO bankTransferResponseDTO);
    public ECheck saveECheck(ECheckResponseDTO eCheckResponseDTO);
    public EMoney saveEmoney(EMoneyResponseDTO eMoneyResponseDTO);

}
