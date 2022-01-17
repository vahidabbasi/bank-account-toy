package com.dkb.service;

import com.dkb.helpers.TransactionReferenceGenerator;
import com.dkb.model.AccountType;
import com.dkb.model.Transaction;
import com.dkb.model.TransactionType;
import com.dkb.model.request.AddUpBalanceRequest;
import com.dkb.exceptions.BankAccountException;
import com.dkb.model.request.TransferMoneyRequest;
import com.dkb.repository.AccountInfoDAO;
import com.dkb.model.Account;
import com.dkb.repository.AccountTransactionsDAO;
import com.dkb.validators.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BankAccountService {

    private final AccountInfoDAO accountInfoDAO;
    private final RequestValidator requestValidator;
    private final AccountTransactionsDAO accountTransactionsDAO;
    private final TransactionReferenceGenerator transactionReferenceGenerator;

    @Autowired
    public BankAccountService(AccountInfoDAO accountInfoDAO,
                              RequestValidator requestValidator,
                              TransactionReferenceGenerator transactionReferenceGenerator,
                              AccountTransactionsDAO accountTransactionsDAO) {
        Objects.requireNonNull(accountInfoDAO, "account info DAO  was null when injected");
        this.accountInfoDAO = accountInfoDAO;
        Objects.requireNonNull(transactionReferenceGenerator, "transaction reference generator  was null when injected");
        this.transactionReferenceGenerator = transactionReferenceGenerator;
        Objects.requireNonNull(accountTransactionsDAO, "account transaction DAO  was null when injected");
        this.accountTransactionsDAO = accountTransactionsDAO;
        Objects.requireNonNull(requestValidator, "request validator DAO  was null when injected");
        this.requestValidator = requestValidator;
    }

    public List<List<Account>> getAllAccounts(List<AccountType> accountTypes) {
        log.info("Getting all account with different account type");
        List<List<Account>> accounts = accountTypes.stream()
                .map(accountType -> accountInfoDAO.getAccounts(accountType.getAccountTypeCode()))
                .collect(Collectors.toList());
        log.info("Retrieved all account with different account type");

        return accounts;
    }

    public BigDecimal getBalance(String accountIban) {
        log.info("Getting the balance of a specified bank account");
        BigDecimal balance = accountInfoDAO.getBalance(accountIban).orElseThrow(() ->
                new BankAccountException("Account with id:" + accountIban + " does not exist.", HttpStatus.NOT_FOUND));
        log.info("Retrieved balance of a specified bank account");

        return balance;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateAccountBalance(AddUpBalanceRequest addUpBalanceRequest) {
        String accountIban = addUpBalanceRequest.getAccountIban();
        log.info("Updating balance account with iban {}", accountIban);
        BigDecimal balance = accountInfoDAO.getBalance(accountIban).orElseThrow(() ->
                new BankAccountException("Account with id:" + accountIban + " does not exist.", HttpStatus.NOT_FOUND));
        accountInfoDAO.updateBalance(accountIban, addUpBalanceRequest.getAmount().add(balance));

        log.info("Insert Transaction");
        String transactionReference = transactionReferenceGenerator.generateUniqueReference();
        accountTransactionsDAO.insertTransaction(accountIban, addUpBalanceRequest.getAmount(), TransactionType.DEPOSIT.name(), transactionReference);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(TransferMoneyRequest transferMoneyRequest) {

        String IbanAccountNumberFrom = transferMoneyRequest.getAccountFromIban();
        String IbanAccountNumberTo = transferMoneyRequest.getAccountToIban();

        Account accountFrom = getAccount(transferMoneyRequest, IbanAccountNumberFrom);
        Account accountTo = getAccount(transferMoneyRequest, IbanAccountNumberTo);

        requestValidator.validateTransferMoneyRequest(transferMoneyRequest, accountFrom, accountTo);

        log.info("Transferring money from account with iban number {} to account with iban number {}", IbanAccountNumberFrom, IbanAccountNumberTo);
        log.info("Update balance");
        accountInfoDAO.updateBalance(IbanAccountNumberFrom, accountFrom.getAccountBalance().subtract(transferMoneyRequest.getAmount()));
        accountInfoDAO.updateBalance(IbanAccountNumberTo, accountTo.getAccountBalance().add(transferMoneyRequest.getAmount()));

        log.info("Insert Transaction");
        String transactionReference = transactionReferenceGenerator.generateUniqueReference();
        accountTransactionsDAO.insertTransaction(IbanAccountNumberFrom, transferMoneyRequest.getAmount(), TransactionType.DEPOSIT.name(), transactionReference);
        accountTransactionsDAO.insertTransaction(IbanAccountNumberTo, transferMoneyRequest.getAmount(), TransactionType.WITHDRAW.name(), transactionReference);
    }

    private Account getAccount(TransferMoneyRequest transferMoneyRequest, String accountFromIban) {
        return accountInfoDAO.getAccount(accountFromIban).orElseThrow(() ->
                new BankAccountException("Account with id:" + transferMoneyRequest.getAccountFromIban() + " does not exist.", HttpStatus.NOT_FOUND));
    }

    public List<Transaction> getAccountTransactions(String accountIbanNumber) {
        log.info("Getting account transactions");
        List<Transaction> transactions = accountTransactionsDAO.getTransactions(accountIbanNumber);
        log.info("Retrieved account transactions");
        return transactions;
    }
}
