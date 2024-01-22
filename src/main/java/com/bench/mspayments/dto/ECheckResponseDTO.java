package com.bench.mspayments.dto;

import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ECheckResponseDTO implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("paymentMethod")
    private PaymentMethod paymentMethod;

    @JsonProperty("type")
    private TypeCurrency type;

    @JsonProperty("state")
    private PaymentState state;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("issue_date")
    private LocalDate issueDate;

    @Transient
    private Long edays;

    @JsonProperty("accountSender")
    private Long accountNumberSender;

    @JsonProperty("accountReceiver")
    private Long accountNumberReceiver;

}