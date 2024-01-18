package com.bench.mspayments.service;

import com.bench.mspayments.dto.BankTransferResponseDTO;
import com.bench.mspayments.dto.ECheckResponseDTO;
import com.bench.mspayments.dto.EMoneyResponseDTO;
import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.Payment;
import org.springframework.data.domain.Page;

public interface PaymentService {
    public Page<PaymentHistoryResponseDTO> filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO, Integer page, Integer size);

    public Account getAccount(Long accountSender);

    public Payment saveBankTransfer(BankTransferResponseDTO bankTransferResponseDTO);

    public Payment saveEheck(ECheckResponseDTO eCheckResponseDTO);

    public Payment saveEmoney(EMoneyResponseDTO eMoneyResponseDTO);

}
