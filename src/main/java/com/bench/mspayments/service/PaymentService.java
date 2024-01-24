package com.bench.mspayments.service;

import com.bench.mspayments.dto.*;
import com.bench.mspayments.model.*;

import java.util.List;
import java.util.Optional;


public interface PaymentService {
    public PaymentDTO filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO);
    public Optional<Account> getAccount(Long accountSender);
    public BankTransfer saveBankTransfer(TransferRequestDTO transferRequestDTO);
    public ECheck saveECheck(TransferRequestDTO transferRequestDTO);
}
