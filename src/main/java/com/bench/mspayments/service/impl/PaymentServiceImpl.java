package com.bench.mspayments.service.impl;

import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.mapper.PaymentMapper;
import com.bench.mspayments.model.Payment;
import com.bench.mspayments.repositories.PaymentRepository;
import com.bench.mspayments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service("serviceRestTemplate")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getFilter(PaymentResponseDTO paymentResponseDTO, Integer page, Integer size) {
        Page<Payment> paymentList = paymentRepository.filter(paymentResponseDTO, PageRequest.of(page, size));
        return paymentList.map(it -> paymentMapper.toDTO(it));
    }
}
