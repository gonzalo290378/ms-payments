package com.bench.mspayments.dto;

import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.bench.mspayments.model.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("issue_date")
    private LocalDate issueDate;

    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    @JsonProperty("accountSender")
    private Long accountSender;

    @JsonProperty("accountReceiver")
    private Long accountReceiver;

}