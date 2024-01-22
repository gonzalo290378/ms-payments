package com.bench.mspayments.repositories;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.model.ECheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("eCheckRepository")
public interface ECheckRepository extends JpaRepository<ECheck, Long> {

    @Query("SELECT eCheck  " +
            "FROM ECheck eCheck " +
            "WHERE (:#{#paymentHistoryResponseDTO.paymentMethod} is null or eCheck.paymentMethod = :#{#paymentHistoryResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentHistoryResponseDTO.type} is null or eCheck.type = :#{#paymentHistoryResponseDTO.type}) " +
            "AND (:#{#paymentHistoryResponseDTO.state} is null or eCheck.state = :#{#paymentHistoryResponseDTO.state}) " +
            "AND (:#{#paymentHistoryResponseDTO.issueDate} is null or eCheck.issueDate >= :#{#paymentHistoryResponseDTO.issueDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.paymentDate} is null or eCheck.paymentDate <= :#{#paymentHistoryResponseDTO.paymentDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberSender} is null or eCheck.accountNumberSender = :#{#paymentHistoryResponseDTO.accountNumberSender}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberReceiver} is null or eCheck.accountNumberReceiver = :#{#paymentHistoryResponseDTO.accountNumberReceiver}) ")
    public List<ECheck> getECheck(@Param("paymentHistoryResponseDTO") PaymentHistoryResponseDTO paymentHistoryResponseDTO);
}

