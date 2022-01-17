package com.dkb.helpers;

import com.dkb.exceptions.BankAccountException;
import com.dkb.repository.AccountTransactionsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TransactionReferenceGenerator {
    // How many times we try to generate a unique transaction reference
    private static final Integer MAX_GENERATION_TRIES = 10;
    private final AccountTransactionsDAO accountTransactionsDAO;

    @Autowired
    public TransactionReferenceGenerator(AccountTransactionsDAO accountTransactionsDAO) {
        Objects.requireNonNull(accountTransactionsDAO, "account transaction DAO  was null when injected");
        this.accountTransactionsDAO = accountTransactionsDAO;
    }

    public String generateUniqueReference() {
        for (int i = 0; i < MAX_GENERATION_TRIES; i++) {
            String transactionReference = UUID.randomUUID().toString().replace("-", "");
            if (0 == accountTransactionsDAO.getNumberOfTransactionReference(transactionReference)) {
                return transactionReference;
            }
        }
        throw new BankAccountException("Could not generate a unique reference in " + MAX_GENERATION_TRIES + " tries.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
