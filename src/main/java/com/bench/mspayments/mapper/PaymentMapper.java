package com.bench.mspayments.mapper;

import com.bench.mspayments.dto.BankTransferDTO;
import com.bench.mspayments.dto.ECheckDTO;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    public List<BankTransferDTO> toBankTransferDTO(List<BankTransfer> bankTransfer);
    public BankTransfer toBankTransferModel(BankTransferDTO bankTransferDTO);
    public ECheckDTO toECheckDTO(ECheck eCheck);

    public List<ECheckDTO> toECheckDTOList(List<ECheck> eCheck);
    public ECheck toECheckModel(ECheckDTO eCheckDTO);
}
