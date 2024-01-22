package com.bench.mspayments.dto;

import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.bench.mspayments.model.Account;
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
public class BankTransferResponseDTO implements Serializable {

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

    @JsonProperty("issueDate")
    private LocalDate issueDate;

    @JsonProperty("paymentDate")
    private LocalDate paymentDate;

    @JsonProperty("accountNumberSender")
    private Long accountNumberSender;

    @JsonProperty("accountNumberReceiver")
    private Long accountNumberReceiver;

    @Transient
    private Long edays;

}