package com.bench.mspayments.service.impl;

import com.bench.mspayments.dto.PaymentDTO;
import com.bench.mspayments.dto.PaymentHistoryResponseDTO;
import com.bench.mspayments.dto.TransferRequestDTO;
import com.bench.mspayments.enums.PaymentMethod;
import com.bench.mspayments.enums.PaymentState;
import com.bench.mspayments.exceptions.AccountNotFoundException;
import com.bench.mspayments.exceptions.AccountSenderEqualReceiverException;
import com.bench.mspayments.exceptions.AccountTypeException;
import com.bench.mspayments.exceptions.InsufficientFundsFoundException;
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

    @Transactional(readOnly = false)
    public BankTransfer saveBankTransfer(TransferRequestDTO transferRequestDTO) {
        checkValidations(transferRequestDTO);

        BankTransfer bankTransfer = new BankTransfer().builder()
                .paymentMethod(PaymentMethod.TRANSFER)
                .type(transferRequestDTO.getType())
                .state(PaymentState.IN_PROCESS)
                .amount(transferRequestDTO.getAmount())
                .issueDate(now())
                .paymentDate(transferRequestDTO.getPaymentDate())
                .accountNumberSender(transferRequestDTO.getAccountNumberSender())
                .accountNumberReceiver(transferRequestDTO.getAccountNumberReceiver())
                .build();

        bankTransfer = bankTransferRepository.save(bankTransfer);

        if (bankTransfer.getPaymentDate().isEqual(now())) {
            jobTransfer();
        }

        return bankTransfer;
    }

    @Transactional(readOnly = false)
    public ECheck saveECheck(TransferRequestDTO transferRequestDTO) {
        transferRequestDTO.setPaymentDate(now().plusDays(transferRequestDTO.getEdays()));
        checkValidations(transferRequestDTO);

        ECheck eCheckTransfer = new ECheck().builder()
                .paymentMethod(PaymentMethod.ECHEQ)
                .type(transferRequestDTO.getType())
                .state(PaymentState.IN_PROCESS)
                .amount(transferRequestDTO.getAmount())
                .issueDate(now())
                .paymentDate(now().plusDays(transferRequestDTO.getEdays()))
                .accountNumberSender(transferRequestDTO.getAccountNumberSender())
                .accountNumberReceiver(transferRequestDTO.getAccountNumberReceiver())
                .build();

        eCheckTransfer = eCheckRepository.save(eCheckTransfer);

        if (eCheckTransfer.getPaymentDate().isEqual(now())) {
            jobTransfer();
        }
        return eCheckTransfer;
    }

    @Transactional(readOnly = true)
    public PaymentDTO filter(PaymentHistoryResponseDTO paymentHistoryResponseDTO) {
        List<BankTransfer> bankTransferList = bankTransferRepository.getBankTransfer(paymentHistoryResponseDTO);
        List<ECheck> echeckList = eCheckRepository.getECheck(paymentHistoryResponseDTO);
        return new PaymentDTO().builder()
                .bankTransfer(bankTransferList)
                .eCheck(echeckList)
                .build();
    }

    private Boolean checkValidations(TransferRequestDTO transferRequestDTO) {
        Optional<Account> accountSender = this.getAccount(transferRequestDTO.getAccountNumberSender());
        Optional<Account> accountReceiver = this.getAccount(transferRequestDTO.getAccountNumberReceiver());
        validateAccount(accountSender, accountReceiver);
        validateIdenticalAccount(accountSender, accountReceiver);
        validateCurrency(transferRequestDTO, accountSender, accountReceiver);
        validateBalance(transferRequestDTO);
        validateFunds(transferRequestDTO, accountSender);
        return true;
    }

    private void validateFunds(TransferRequestDTO transferRequestDTO, Optional<Account> accountSender) {
        if (accountSender.get().getBalance() < transferRequestDTO.getAmount())
            throw new InsufficientFundsFoundException("The account does have not enough founds");
    }

    private void validateBalance(TransferRequestDTO transferRequestDTO) {
        if (transferRequestDTO.getPaymentDate().isBefore(now()))
            throw new AccountNotFoundException("The payment date must be equals or greater than today");
    }

    private void validateCurrency(TransferRequestDTO transferRequestDTO, Optional<Account> accountSender,
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
        return Optional.ofNullable(restTemplate.getForObject("http://localhost:8090/ms-accounts/api/v1/accounts/{accountNumber}",
                Account.class, uriPathVariable));
    }

    private List<BankTransfer> bankTransferList() {
        return bankTransferRepository
                .findAll()
                .stream()
                .filter(transfer -> transfer.getPaymentDate().isEqual(now()) && transfer.getState().equals(PaymentState.IN_PROCESS))
                .collect(Collectors.toList());
    }

    private List<ECheck> echeckList() {
        return eCheckRepository
                .findAll()
                .stream()
                .filter(transfer -> transfer.getPaymentDate().isEqual(now()) && transfer.getState().equals(PaymentState.IN_PROCESS))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void jobTransfer() {
        List<BankTransfer> bankTransferList = bankTransferList();
        List<ECheck> echeckTransferList = echeckList();
        if (!bankTransferList.isEmpty()) makeBankTransfer(bankTransferList);
        if (!echeckTransferList.isEmpty()) makeEcheckTransfer(echeckTransferList);
    }

    @Transactional(readOnly = false)
    public List<BankTransfer> makeBankTransfer(List<BankTransfer> bankTransferList) {

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

    @Transactional(readOnly = false)

    public List<ECheck> makeEcheckTransfer(List<ECheck> makeEcheckTransfer) {

        for (ECheck eCheckTransfer : makeEcheckTransfer) {
            Optional<Account> accountSender = this.getAccount(eCheckTransfer.getAccountNumberSender());
            Optional<Account> accountReceiver = this.getAccount(eCheckTransfer.getAccountNumberReceiver());

            if (accountSender.get().getBalance() - eCheckTransfer.getAmount() >= 0) {
                accountSender.get().setBalance(accountSender.get().getBalance() - eCheckTransfer.getAmount());
                accountReceiver.get().setBalance(accountReceiver.get().getBalance() + eCheckTransfer.getAmount());
                saveAccount(accountSender.get());
                saveAccount(accountReceiver.get());
                eCheckTransfer.setState(PaymentState.APPROVED);
                eCheckRepository.save(eCheckTransfer);
            } else {
                eCheckTransfer.setState(PaymentState.REJECTED);
                eCheckRepository.save(eCheckTransfer);
            }
        }
        return makeEcheckTransfer;
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
