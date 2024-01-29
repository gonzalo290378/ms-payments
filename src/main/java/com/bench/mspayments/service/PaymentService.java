package com.bench.mspayments.service;

import com.bench.mspayments.dto.ECheckDTO;
import com.bench.mspayments.dto.PaymentDTO;
import com.bench.mspayments.dto.PaymentRequestDTO;
import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public interface PaymentService {
    public PaymentDTO filter(PaymentResponseDTO paymentResponseDTO);

    public Optional<Account> getAccount(Long accountSender);

    public BankTransfer saveBankTransfer(PaymentRequestDTO transferRequestDTO);

    public ECheck saveECheck(PaymentRequestDTO transferRequestDTO);

    public List<ECheckDTO> findProcessedByQueue();

    public void jobTransfer();

    public void echeckTransfer(ECheckDTO eCheckDTO);

    public ResponseEntity<Account> save(Account account);

    public ECheck update(Long id, ECheck eCheck);

}
