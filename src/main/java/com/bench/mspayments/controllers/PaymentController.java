package com.bench.mspayments.controllers;

import com.bench.mspayments.dto.ECheckDTO;
import com.bench.mspayments.dto.PaymentDTO;
import com.bench.mspayments.dto.PaymentRequestDTO;
import com.bench.mspayments.dto.PaymentResponseDTO;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
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

        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
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
        return ResponseEntity.ok(paymentServiceImpl.filter(paymentResponseDTO));
    }

    @PostMapping("/processTransfer")
    public ResponseEntity<BankTransfer> processTransfer(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        log.info("Calling saveBankTransfer with {}", paymentRequestDTO);
        return ResponseEntity.ok(paymentServiceImpl.saveBankTransfer(paymentRequestDTO));
    }

    @PostMapping("/processEcheck")
    public ResponseEntity<ECheck> processEcheck(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        log.info("Calling saveEheck with {}", paymentRequestDTO);
        return ResponseEntity.ok(paymentServiceImpl.saveECheck(paymentRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ECheck> update(@PathVariable() Long id, @RequestBody ECheck eCheck) {
        log.info("Calling update with {}", eCheck);
        return ResponseEntity.ok(paymentServiceImpl.update(id, eCheck));
    }

    @GetMapping("/findProcessedByQueue")
    public ResponseEntity<List<ECheckDTO>> findProcessedByQueue() {
        log.info("Calling queue with {}");
        return ResponseEntity.ok(paymentServiceImpl.findProcessedByQueue());
    }

    @GetMapping("/getAccount")
    public ResponseEntity<Account> getAccount(Long accountNumber) {
        log.info("Calling getAccount with {}");
        return ResponseEntity.ok(paymentServiceImpl.getAccount(accountNumber).get());
    }

    @GetMapping("/job-transfer")
    public void jobTransfer() {
        log.info("Calling jobTransfer with {}");
        paymentServiceImpl.jobTransfer();
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
