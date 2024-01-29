package com.bench.mspayments.repositories;

import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.model.BankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("bankTransferRepository")
public interface BankTransferRepository extends JpaRepository<BankTransfer, Long> {

    @Query("SELECT bankTransfer  " +
            "FROM BankTransfer bankTransfer " +
            "WHERE (:#{#paymentHistoryResponseDTO.paymentMethod} is null or bankTransfer.paymentMethod = :#{#paymentHistoryResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentHistoryResponseDTO.type} is null or bankTransfer.type = :#{#paymentHistoryResponseDTO.type}) " +
            "AND (:#{#paymentHistoryResponseDTO.state} is null or bankTransfer.state = :#{#paymentHistoryResponseDTO.state}) " +
            "AND (:#{#paymentHistoryResponseDTO.issueDate} is null or bankTransfer.issueDate >= :#{#paymentHistoryResponseDTO.issueDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.paymentDate} is null or bankTransfer.paymentDate <= :#{#paymentHistoryResponseDTO.paymentDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberSender} is null or bankTransfer.accountNumberSender = :#{#paymentHistoryResponseDTO.accountNumberSender}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberReceiver} is null or bankTransfer.accountNumberReceiver = :#{#paymentHistoryResponseDTO.accountNumberReceiver}) ")
    public List<BankTransfer> getBankTransfer(@Param("paymentHistoryResponseDTO") PaymentResponseDTO paymentHistoryResponseDTO);
}

