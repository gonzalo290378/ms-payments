package com.bench.mspayments.repositories;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.model.EMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("emoneyRepository")
public interface EmoneyRepository extends JpaRepository<EMoney, Long> {

    @Query("SELECT eMoney  " +
            "FROM EMoney eMoney " +
            "WHERE (:#{#paymentHistoryResponseDTO.paymentMethod} is null or eMoney.paymentMethod = :#{#paymentHistoryResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentHistoryResponseDTO.type} is null or eMoney.type = :#{#paymentHistoryResponseDTO.type}) " +
            "AND (:#{#paymentHistoryResponseDTO.state} is null or eMoney.state = :#{#paymentHistoryResponseDTO.state}) " +
            "AND (:#{#paymentHistoryResponseDTO.issueDate} is null or eMoney.issueDate >= :#{#paymentHistoryResponseDTO.issueDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.paymentDate} is null or eMoney.paymentDate <= :#{#paymentHistoryResponseDTO.paymentDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberSender} is null or eMoney.dniSender = :#{#paymentHistoryResponseDTO.accountNumberSender}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberReceiver} is null or eMoney.dniReceiver = :#{#paymentHistoryResponseDTO.accountNumberReceiver}) ")
    public List<EMoney> getEmoney(@Param("paymentHistoryResponseDTO") PaymentHistoryResponseDTO paymentHistoryResponseDTO);
}

