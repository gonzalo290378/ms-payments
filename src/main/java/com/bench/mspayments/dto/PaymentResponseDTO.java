package com.bench.mspayments.dto;

import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
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
public class PaymentResponseDTO implements Serializable {

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
    private LocalDate issue_date;

    @JsonProperty("payment_date")
    private LocalDate payment_date;

    @JsonProperty("account_sender")
    private Long account_sender;

    @JsonProperty("account_receiver")
    private Long account_receiver;

}