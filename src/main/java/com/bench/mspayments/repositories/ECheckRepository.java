package com.bench.mspayments.repositories;

import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.model.ECheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository("eCheckRepository")
public interface ECheckRepository extends JpaRepository<ECheck, Long> {

    @Query("SELECT eCheck  " +
            "FROM ECheck eCheck " +
            "WHERE (:#{#paymentResponseDTO.paymentMethod} is null or eCheck.paymentMethod = :#{#paymentResponseDTO.paymentMethod}) " +
            "AND (:#{#paymentResponseDTO.type} is null or eCheck.type = :#{#paymentResponseDTO.type}) " +
            "AND (:#{#paymentResponseDTO.state} is null or eCheck.state = :#{#paymentResponseDTO.state}) " +
            "AND (:#{#paymentResponseDTO.issueDate} is null or eCheck.issueDate >= :#{#paymentResponseDTO.issueDate}) " +
            "AND (:#{#paymentResponseDTO.paymentDate} is null or eCheck.paymentDate <= :#{#paymentResponseDTO.paymentDate}) " +
            "AND (:#{#paymentResponseDTO.accountNumberSender} is null or eCheck.accountNumberSender = :#{#paymentResponseDTO.accountNumberSender}) " +
            "AND (:#{#paymentResponseDTO.accountNumberReceiver} is null or eCheck.accountNumberReceiver = :#{#paymentResponseDTO.accountNumberReceiver}) ")
    public List<ECheck> getECheck(@Param("paymentResponseDTO") PaymentResponseDTO paymentResponseDTO);

    public Optional<ECheck> findById(@Param("id") Long id);
}

