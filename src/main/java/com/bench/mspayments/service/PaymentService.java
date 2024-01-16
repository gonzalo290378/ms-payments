package com.bench.mspayments.service;

import com.bench.mspayments.dto.PaymentResponseDTO;
import org.springframework.data.domain.Page;

public interface PaymentService {

    public Page<PaymentResponseDTO> getFilter(PaymentResponseDTO paymentResponseDTO, Integer page, Integer size);

}