package com.bench.mspayments.service.impl;

import com.bench.mspayments.dto.BankTransferResponseDTO;
import com.bench.mspayments.dto.ECheckResponseDTO;
import com.bench.mspayments.dto.EMoneyResponseDTO;
import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.mapper.PaymentMapper;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.Payment;
import com.bench.mspayments.repositories.PaymentRepository;
import com.bench.mspayments.service.PaymentService;
import com.bench.mspayments.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;

@Service("serviceRestTemplate")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    private Validator validator;


    @Transactional(readOnly = true)
    public Account getAccount(Long accountNumber) {
        HashMap<String, Long> uriPathVariable = new HashMap<>();
        uriPathVariable.put("accountNumber", accountNumber);
        return restTemplate.getForObject("http://localhost:8090/ms-accounts/api/v1/accounts/{accountNumber}", Account.class, uriPathVariable);
    }

    @Transactional(readOnly = false)
    public Payment saveBankTransfer(BankTransferResponseDTO bankTransferResponseDTO) {
        return null;
    }

    @Transactional(readOnly = false)
    public Payment saveEheck(ECheckResponseDTO eCheckResponseDTO) {
        return null;
    }

    @Transactional(readOnly = false)
    public Payment saveEmoney(EMoneyResponseDTO eMoneyResponseDTO) {
        return null;
    }


    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponseDTO> filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO, Integer page, Integer size) {
        Page<Payment> paymentList = paymentRepository.getPaymentHistory(paymentHistoryResponseDTO, PageRequest.of(page, size));
        return paymentList.map(it -> paymentMapper.toDTO(it));
    }

}
