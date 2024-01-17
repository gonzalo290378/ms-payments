package com.bench.mspayments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long id;

    private Long accountNumber;

    private String type;

    private Double balance;

    private Boolean state;

    private Long userId;

}

