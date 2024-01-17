package com.bench.mspayments.service;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.model.Account;
import org.springframework.data.domain.Page;

public interface PaymentService {
    public Page<PaymentHistoryResponseDTO> filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO, Integer page, Integer size);
    public Account getAccount(Long accountSender);
}