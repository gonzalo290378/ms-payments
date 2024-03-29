package com.bench.mspayments.dto;

import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

//TODO: LOS DTO PUEDEN CONTEMPLAR ENTIDADES COMO ATRIBUTOS?
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO implements Serializable {

    List<BankTransfer> bankTransfer;

    List<ECheck> eCheck;

}