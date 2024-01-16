package com.bench.mspayments.model;

import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TypeCurrency type;

    @NotNull(message = "Balance should have a number")
    @Column(name = "amount")
    private Double amount;

    @Column(name = "issue_date")
    private LocalDate issue_date;

    @Column(name = "payment_date")
    private LocalDate payment_date;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @NotNull(message = "Account sender should have an account number")
    @Column(name = "account_sender")
    private Long account_sender;

    @NotNull(message = "Account receiver should have an account number")
    @Column(name = "account_receiver")
    private Long account_receiver;

}
