package com.bench.mspayments.repositories;

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
            "WHERE (:#{#paymentResponseDTO.paymentMethod} is null or payments.paymentMethod = :#{#paymentResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentResponseDTO.type} is null or payments.type = :#{#paymentResponseDTO.type}) " +
            "AND (:#{#paymentResponseDTO.state} is null or payments.state = :#{#paymentResponseDTO.state}) " +
            "AND (:#{#paymentResponseDTO.account_sender} is null or payments.account_sender = :#{#paymentResponseDTO.account_sender}) " +
            "AND (:#{#paymentResponseDTO.account_receiver} is null or payments.account_receiver = :#{#paymentResponseDTO.account_receiver}) ")

    Page<Payment> filter(@Param("paymentResponseDTO") PaymentResponseDTO paymentResponseDTO, Pageable pageable);
}

