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
@Table(name = "e_money")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EMoney {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TypeCurrency type;

    @NotNull(message = "Amount should have a number")
    @Column(name = "amount")
    private Double amount;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @NotNull(message = "DNI sender should have an account number")
    @Column(name = "dni_sender")
    private Long dniSender;

    @NotNull(message = "DNI receiver should have an account number")
    @Column(name = "dni_receiver")
    private Long dniReceiver;

}
