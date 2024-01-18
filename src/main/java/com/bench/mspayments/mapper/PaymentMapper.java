package com.bench.mspayments.mapper;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    public PaymentHistoryResponseDTO toDTO(Payment payment);
    public Payment toModel(PaymentHistoryResponseDTO paymentHistoryResponseDTO);

}
