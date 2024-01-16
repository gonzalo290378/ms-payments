package com.bench.mspayments.mapper;

import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    public PaymentResponseDTO toDTO(Payment payment);

    public Payment toModel(PaymentResponseDTO paymentResponseDTO);
}
