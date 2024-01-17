package com.bench.mspayments.controllers;

import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.enums.TypeCurrency;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.service.impl.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
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
    public ResponseEntity<Page<PaymentHistoryResponseDTO>> filter(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "accountSender", required = true) Long accountNumberSender,
            @RequestParam(name = "accountReceiver", required = false) Long accountNumberReceiver,
            @RequestParam(name = "issueDate", required = false) LocalDate issueDate,
            @RequestParam(name = "paymentDate", required = false) LocalDate paymentDate,
            @RequestParam(name = "paymentMethod", required = false) PaymentMethod paymentMethod,
            @RequestParam(name = "type", required = false) TypeCurrency type,
            @RequestParam(name = "state", required = false) PaymentState state) {

        PaymentHistoryResponseDTO paymentHistoryResponseDTO = PaymentHistoryResponseDTO.builder()
                .accountNumberSender(accountNumberSender)
                .accountNumberReceiver(accountNumberReceiver)
                .issueDate(issueDate)
                .paymentDate(paymentDate)
                .paymentMethod(paymentMethod)
                .type(type)
                .state(state)
                .build();
        return ResponseEntity.ok(paymentServiceImpl.filter(paymentHistoryResponseDTO, page, size));
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
