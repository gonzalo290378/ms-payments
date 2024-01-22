package com.bench.mspayments.controllers;

import com.bench.mspayments.dto.*;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import com.bench.mspayments.model.EMoney;
import com.bench.mspayments.service.impl.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("api/v1/payments")
public class PaymentController {

    @Autowired
    private Environment environment;

    @Autowired
    @Qualifier("serviceRestTemplate")
    private PaymentServiceImpl paymentServiceImpl;

    //LOAD BALANCER
    @Value("${config.balanced.test}")
    private String balancerTest;

    //CONFIG-SERVER
    @Value("${configuration.text}")
    private String text;

    @GetMapping("/filter")
    public ResponseEntity<PaymentDTO> filter(
            @RequestParam(name = "accountSender", required = false) Long accountNumberSender,
            @RequestParam(name = "accountReceiver", required = false) Long accountNumberReceiver,
            @RequestParam(name = "issueDate", required = false) LocalDate issueDate,
            @RequestParam(name = "paymentDate", required = false) LocalDate paymentDate,
            @RequestParam(name = "dniSender", required = false) Long dniSender,
            @RequestParam(name = "dniReceiver", required = false) Long dniReceiver,
            @RequestParam(name = "paymentMethod", required = false) PaymentMethod paymentMethod,
            @RequestParam(name = "type", required = false) TypeCurrency type,
            @RequestParam(name = "state", required = false) PaymentState state) {

        PaymentHistoryResponseDTO paymentHistoryResponseDTO = PaymentHistoryResponseDTO.builder()
                .accountNumberSender(accountNumberSender)
                .accountNumberReceiver(accountNumberReceiver)
                .issueDate(issueDate)
                .paymentDate(paymentDate)
                .paymentMethod(paymentMethod)
                .dniReceiver(dniReceiver)
                .dniSender(dniSender)
                .type(type)
                .state(state)
                .build();
        return ResponseEntity.ok(paymentServiceImpl.filter(paymentHistoryResponseDTO));
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<BankTransfer> saveBankTransfer(@RequestBody BankTransferResponseDTO bankTransferResponseDTO) {
        log.info("Calling saveBankTransfer with {}", bankTransferResponseDTO);
        return ResponseEntity.ok(paymentServiceImpl.saveBankTransfer(bankTransferResponseDTO));
    }

    @PostMapping("/e-check")
    public ResponseEntity<ECheck> saveEheck(@RequestBody ECheckResponseDTO eCheckResponseDTO) {
        log.info("Calling saveEheck with {}", eCheckResponseDTO);
        return ResponseEntity.ok(paymentServiceImpl.saveECheck(eCheckResponseDTO));
    }

    @PostMapping("/e-money")
    public ResponseEntity<EMoney> saveEmoney(@RequestBody EMoneyResponseDTO eMoneyResponseDTO) {
        log.info("Calling saveEmoney with {}", eMoneyResponseDTO);
        return ResponseEntity.ok(paymentServiceImpl.saveEmoney(eMoneyResponseDTO));
    }

    @GetMapping("/job-transfer")
    public List<BankTransfer> jobTransfer() {
        log.info("Calling jobTransfer with {}");
        return paymentServiceImpl.jobTransfer();
    }


    @GetMapping("/get-config")
    public ResponseEntity<?> getConfig(@Value("${server.port}") String port) {
        log.info("getConfig {}" + " port: " + port);
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);

        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
            json.put("env", environment.getActiveProfiles()[0]);
        }
        return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
    }

    @GetMapping("/load-balancer")
    public ResponseEntity<?> loadBalancer() {
        log.info("Calling loadBalancer with {}");
        Map<String, Object> response = new HashMap<>();
        response.put("loadBalancer", balancerTest);
        //response.put("users", accountServiceImpl.findAll());
        return ResponseEntity.ok(response);
    }


}
