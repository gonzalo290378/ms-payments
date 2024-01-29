package com.bench.mspayments.service.impl;

import com.bench.mspayments.dto.*;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.exceptions.*;
import com.bench.mspayments.mapper.PaymentMapper;
import com.bench.mspayments.model.Account;
import com.bench.mspayments.model.BankTransfer;
import com.bench.mspayments.model.ECheck;
import com.bench.mspayments.repositories.BankTransferRepository;
import com.bench.mspayments.repositories.ECheckRepository;
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
    private ECheckRepository eCheckRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Transactional(readOnly = false)
    public BankTransfer saveBankTransfer(PaymentRequestDTO paymentRequestDTO) {
        checkValidations(paymentRequestDTO);

        BankTransfer bankTransfer = new BankTransfer().builder()
                .paymentMethod(PaymentMethod.TRANSFER)
                .type(paymentRequestDTO.getType())
                .state(PaymentState.IN_PROCESS)
                .amount(paymentRequestDTO.getAmount())
                .issueDate(now())
                .paymentDate(paymentRequestDTO.getPaymentDate())
                .accountNumberSender(paymentRequestDTO.getAccountNumberSender())
                .accountNumberReceiver(paymentRequestDTO.getAccountNumberReceiver())
                .build();

        Optional<BankTransfer> newBankTransfer = Optional.ofNullable(bankTransferRepository.save(bankTransfer));
        if (newBankTransfer.isPresent()) jobTransfer();
        return newBankTransfer.get();
    }

    @Transactional(readOnly = false)
    public ECheck saveECheck(PaymentRequestDTO paymentRequestDTO) {
        paymentRequestDTO.setPaymentDate(now().plusDays(paymentRequestDTO.getEdays()));
        checkValidations(paymentRequestDTO);

        ECheck eCheckTransfer = new ECheck().builder()
                .paymentMethod(PaymentMethod.ECHEQ)
                .type(paymentRequestDTO.getType())
                .state(PaymentState.DEFERRED_PAYMENT)
                .amount(paymentRequestDTO.getAmount())
                .issueDate(now())
                .paymentDate(now().plusDays(paymentRequestDTO.getEdays()))
                .accountNumberSender(paymentRequestDTO.getAccountNumberSender())
                .accountNumberReceiver(paymentRequestDTO.getAccountNumberReceiver())
                .build();

        Optional<ECheck> newEcheckTransfer = Optional.ofNullable(eCheckRepository.save(eCheckTransfer));
        if (newEcheckTransfer.isPresent()) echeckTransfer(paymentMapper.toECheckDTO(newEcheckTransfer.get()));
        return newEcheckTransfer.get();
    }

    @Transactional(readOnly = true)
    public PaymentDTO filter(PaymentResponseDTO paymentResponseDTO) {
        List<BankTransfer> bankTransferList = bankTransferRepository.getBankTransfer(paymentResponseDTO);
        List<ECheck> echeckList = eCheckRepository.getECheck(paymentResponseDTO);
        return new PaymentDTO().builder()
                .bankTransfer(bankTransferList)
                .eCheck(echeckList)
                .build();
    }

    private Boolean checkValidations(PaymentRequestDTO paymentRequestDTO) {
        Optional<Account> accountSender = this.getAccount(paymentRequestDTO.getAccountNumberSender());
        Optional<Account> accountReceiver = this.getAccount(paymentRequestDTO.getAccountNumberReceiver());
        validateAccount(accountSender, accountReceiver);
        validateIdenticalAccount(accountSender, accountReceiver);
        validateCurrency(paymentRequestDTO, accountSender, accountReceiver);
        validatePayment(paymentRequestDTO);
        validateFunds(paymentRequestDTO, accountSender);
        return true;
    }

    private void validateFunds(PaymentRequestDTO transferRequestDTO, Optional<Account> accountSender) {
        if (accountSender.get().getBalance() < transferRequestDTO.getAmount())
            throw new InsufficientFundsFoundException("The account does have not enough founds");
    }

    private void validatePayment(PaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO.getPaymentDate().isBefore(now()))
            throw new AccountNotFoundException("The payment date must be equals or greater than today");
    }

    private void validateCurrency(PaymentRequestDTO transferRequestDTO, Optional<Account> accountSender,
                                  Optional<Account> accountReceiver) {
        if (!accountSender.get().getType().equalsIgnoreCase(accountReceiver.get().getType()) || !accountSender.get().getType().equalsIgnoreCase(transferRequestDTO.getType().getType()))
            throw new AccountTypeException("The account types must be in the same currency");
    }

    private void validateIdenticalAccount(Optional<Account> accountSender, Optional<Account> accountReceiver) {
        if (accountSender.get().getAccountNumber().equals(accountReceiver.get().getAccountNumber()))
            throw new AccountSenderEqualReceiverException("The accounts can not be identical");
    }

    private void validateAccount(Optional<Account> accountSender, Optional<Account> accountReceiver) {
        if (accountSender.isEmpty() && accountReceiver.isEmpty())
            throw new AccountNotFoundException("The accounts must exist");
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccount(Long accountNumber) {
        HashMap<String, Long> uriPathVariable = new HashMap<>();
        uriPathVariable.put("accountNumber", accountNumber);
        return Optional.ofNullable(restTemplate.
                getForObject("http://localhost:8090/ms-accounts/api/v1/accounts/{accountNumber}",
                        Account.class, uriPathVariable));
    }

    private List<BankTransfer> bankTransferList() {
        return bankTransferRepository
                .findAll()
                .stream()
                .filter(transfer -> transfer.getPaymentDate().isEqual(now()) &&
                        transfer.getState().equals(PaymentState.IN_PROCESS))
                .collect(Collectors.toList());
    }

    public List<ECheckDTO> findProcessedByQueue() {
        List<ECheck> echeckList = eCheckRepository
                .findAll()
                .stream()
                .filter(transfer -> {
                    return transfer.getState().equals(PaymentState.PROCESSED_BY_QUEUE) ||
                            transfer.getState().equals(PaymentState.DEFERRED_PAYMENT);
                })
                .collect(Collectors.toList());
        return paymentMapper.toECheckDTOList(echeckList);
    }

    @Transactional(readOnly = true)
    public void jobTransfer() {
        List<BankTransfer> bankTransferList = bankTransferList();
        List<BankTransferDTO> bankTransferDTO = paymentMapper.toBankTransferDTO(bankTransferList);
        if (!bankTransferDTO.isEmpty()) {
            for (BankTransferDTO bankTransfer : bankTransferDTO) {
                Optional<Account> accountSender = this.getAccount(bankTransfer.getAccountNumberSender());
                Optional<Account> accountReceiver = this.getAccount(bankTransfer.getAccountNumberReceiver());
                transferProcess(bankTransfer, accountSender, accountReceiver);
            }
        }
    }

    private BankTransfer transferProcess(BankTransferDTO bankTransfer, Optional<Account> accountSender, Optional<Account> accountReceiver) {
        if (accountSender.get().getBalance() - bankTransfer.getAmount() >= 0) {
            accountSender.get().setBalance(accountSender.get().getBalance() - bankTransfer.getAmount());
            accountReceiver.get().setBalance(accountReceiver.get().getBalance() + bankTransfer.getAmount());
            save(accountSender.get());
            save(accountReceiver.get());
            bankTransfer.setState(PaymentState.APPROVED);
        } else {
            bankTransfer.setState(PaymentState.REJECTED);
        }
        return bankTransferRepository.save(paymentMapper.toBankTransferModel(bankTransfer));
    }

    @Transactional(readOnly = true)
    public void echeckTransfer(ECheckDTO eCheckDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ECheckDTO> entity = new HttpEntity<>(eCheckDTO, headers);
        restTemplate.exchange("http://localhost:8090/ms-queue/api/v1/queue/deferred-payment",
                HttpMethod.POST, entity, ECheckDTO.class);
    }


    public ResponseEntity<Account> save(Account account) {
        HashMap<String, Long> uriPathVariable = new HashMap<>();
        Long id = account.getAccountNumber();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        uriPathVariable.put("id", id);
        return restTemplate.exchange("http://localhost:8090/ms-accounts/api/v1/accounts/{id}", HttpMethod.PUT, entity, Account.class, uriPathVariable);
    }

    @Transactional(readOnly = false)
    public ECheck update(Long id, ECheck eCheck) {
        if (Optional.of(eCheck).isPresent()) return eCheckRepository.save(eCheck);
        throw new EcheckNotFoundException("id: " + id + " does not exist");
    }

    private LocalDate now() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        return localDateTime.toLocalDate();
    }

}
