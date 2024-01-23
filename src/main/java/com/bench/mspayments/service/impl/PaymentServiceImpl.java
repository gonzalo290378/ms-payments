package com.bench.mspayments.service.impl;

import com.bench.mspayments.dto.*;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.exceptions.AccountNotFoundException;
import com.bench.mspayments.exceptions.AccountSenderEqualReceiverException;
import com.bench.mspayments.exceptions.AccountTypeException;
import com.bench.mspayments.exceptions.InsufficientFundsFoundException;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import com.bench.mspayments.model.EMoney;
import com.bench.mspayments.repositories.BankTransferRepository;
import com.bench.mspayments.repositories.ECheckRepository;
import com.bench.mspayments.repositories.EmoneyRepository;
import com.bench.mspayments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("serviceRestTemplate")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Autowired
    private EmoneyRepository emoneyRepository;

    @Autowired
    private ECheckRepository eCheckRepository;

    @Transactional(readOnly = false)
    public BankTransfer saveBankTransfer(BankTransferResponseDTO bankTransferDTO) {
        validations(bankTransferDTO);

        BankTransfer bankTransfer = new BankTransfer().builder()
                .paymentMethod(PaymentMethod.TRANSFER)
                .type(bankTransferDTO.getType())
                .state(PaymentState.IN_PROCESS)
                .amount(bankTransferDTO.getAmount())
                .issueDate(now())
                .paymentDate(bankTransferDTO.getPaymentDate())
                .accountNumberSender(bankTransferDTO.getAccountNumberSender())
                .accountNumberReceiver(bankTransferDTO.getAccountNumberReceiver())
                .build();

        bankTransfer = bankTransferRepository.save(bankTransfer);

        if (bankTransfer.getPaymentDate().isEqual(now())) {
            jobTransfer();
        }

        return bankTransfer;
    }

    @Transactional(readOnly = false)
    public ECheck saveECheck(ECheckResponseDTO eCheckResponseDTO) {
        return null;
    }

    @Transactional(readOnly = false)
    public EMoney saveEmoney(EMoneyResponseDTO eMoneyResponseDTO) {
        return null;
    }

    @Transactional(readOnly = true)
    public PaymentDTO filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO) {
        List<BankTransfer> bankTransferList = bankTransferRepository.getBankTransfer(paymentHistoryResponseDTO);
        List<EMoney> emoneyList = emoneyRepository.getEmoney(paymentHistoryResponseDTO);
        List<ECheck> echeckList = eCheckRepository.getECheck(paymentHistoryResponseDTO);
        return new PaymentDTO().builder()
                .bankTransfer(bankTransferList)
                .eMoney(emoneyList)
                .eCheck(echeckList)
                .build();
    }

    private Boolean validations(BankTransferResponseDTO bankTransferDTO) {
        Optional<Account> accountSender = this.getAccount(bankTransferDTO.getAccountNumberSender());
        Optional<Account> accountReceiver = this.getAccount(bankTransferDTO.getAccountNumberReceiver());

        if (accountSender.isEmpty() && accountReceiver.isEmpty())
            throw new AccountNotFoundException("The accounts must exist");

        if (accountSender.get().getAccountNumber().equals(accountReceiver.get().getAccountNumber()))
            throw new AccountSenderEqualReceiverException("The accounts can not be identical");

        if (!accountSender.get().getType().equalsIgnoreCase(accountReceiver.get().getType()) || !accountSender.get()
                .getType().equalsIgnoreCase(bankTransferDTO.getType().getType()))
            throw new AccountTypeException("The account types must be in the same currency");

        if (bankTransferDTO.getPaymentDate().isBefore(now()))
            throw new AccountNotFoundException("The payment date must be equals or greater than today");

        if (accountSender.get().getBalance() < bankTransferDTO.getAmount())
            throw new InsufficientFundsFoundException("The account does have not enough founds");

        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccount(Long accountNumber) {
        HashMap<String, Long> uriPathVariable = new HashMap<>();
        uriPathVariable.put("accountNumber", accountNumber);
        return Optional.ofNullable(restTemplate.getForObject("http://localhost:8090/ms-accounts/api/v1/accounts/{accountNumber}", Account.class, uriPathVariable));
    }

    public List<BankTransfer> jobTransfer() {
        List<BankTransfer> bankTransferList = bankTransferRepository
                .findAll()
                .stream()
                .filter(transfer -> transfer.getPaymentDate().isEqual(now()) && transfer.getState().equals(PaymentState.IN_PROCESS))
                .collect(Collectors.toList());

        for (BankTransfer bankTransfer : bankTransferList) {

            Optional<Account> accountSender = this.getAccount(bankTransfer.getAccountNumberSender());
            Optional<Account> accountReceiver = this.getAccount(bankTransfer.getAccountNumberReceiver());

            if (accountSender.get().getBalance() - bankTransfer.getAmount() >= 0) {
                accountSender.get().setBalance(accountSender.get().getBalance() - bankTransfer.getAmount());
                accountReceiver.get().setBalance(accountReceiver.get().getBalance() + bankTransfer.getAmount());
                saveAccount(accountSender.get());
                saveAccount(accountReceiver.get());
                bankTransfer.setState(PaymentState.APPROVED);
                bankTransferRepository.save(bankTransfer);

            } else {
                bankTransfer.setState(PaymentState.REJECTED);
                bankTransferRepository.save(bankTransfer);
            }
        }
        return bankTransferList;
    }

    public ResponseEntity<Account> saveAccount(Account account) {
        HashMap<String, Long> uriPathVariable = new HashMap<>();
        Long id = account.getAccountNumber();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        uriPathVariable.put("id", id);
        return restTemplate.exchange("http://localhost:8090/ms-accounts/api/v1/accounts/{id}", HttpMethod.PUT, entity, Account.class, uriPathVariable);
    }

    public LocalDate now() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        return localDateTime.toLocalDate();
    }

}
