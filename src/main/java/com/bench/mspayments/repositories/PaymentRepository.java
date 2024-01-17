package com.bench.mspayments.repositories;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("paymentRepository")
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT payments  " +
            "FROM Payment payments " +
            "WHERE (:#{#paymentHistoryResponseDTO.paymentMethod} is null or payments.paymentMethod = :#{#paymentHistoryResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentHistoryResponseDTO.type} is null or payments.type = :#{#paymentHistoryResponseDTO.type}) " +
            "AND (:#{#paymentHistoryResponseDTO.state} is null or payments.state = :#{#paymentHistoryResponseDTO.state}) " +
            "AND (:#{#paymentHistoryResponseDTO.issueDate} is null or payments.issueDate = :#{#paymentHistoryResponseDTO.issueDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.paymentDate} is null or payments.paymentDate = :#{#paymentHistoryResponseDTO.paymentDate}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberSender} is null or payments.accountNumberSender = :#{#paymentHistoryResponseDTO.accountNumberSender}) " +
            "AND (:#{#paymentHistoryResponseDTO.accountNumberReceiver} is null or payments.accountNumberReceiver = :#{#paymentHistoryResponseDTO.accountNumberReceiver}) ")

    Page<Payment> getPaymentHistory(@Param("paymentHistoryResponseDTO") PaymentHistoryResponseDTO paymentHistoryResponseDTO, Pageable pageable);
}

