package com.dkb.repository;

import com.dkb.model.Transaction;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;

public interface AccountTransactionsDAO {

    @SqlUpdate("insert into ACCOUNT_TRANSACTIONS (ACCOUNT_IBAN, AMOUNT, TRANSACTION_TYPE, TRANSACTION_REFERENCE) values (:accountIban, :amount, :transactionType, :transactionReference)")
    void insertTransaction(@Bind("accountIban") String accountIban, @Bind("amount") BigDecimal amount,
                           @Bind("transactionType") String transactionType, @Bind("transactionReference") String transactionReference);

    @SqlQuery("SELECT COUNT(*) FROM ACCOUNT_TRANSACTIONS WHERE TRANSACTION_REFERENCE = :transactionReference")
    Integer getNumberOfTransactionReference(@Bind("transactionReference") String transactionReference);

    @SqlQuery("SELECT * FROM ACCOUNT_TRANSACTIONS WHERE ACCOUNT_IBAN = :accountIban")
    @RegisterConstructorMapper(Transaction.class)
    List<Transaction> getTransactions(@Bind("accountIban") String accountIban);
}
