package com.dkb.repository;

import com.dkb.model.Account;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountInfoDAO {

    @SqlQuery("SELECT * FROM ACCOUNT_INFO WHERE ACCOUNT_TYPE = :accountType")
    @RegisterConstructorMapper(Account.class)
    List<Account> getAccounts(@Bind("accountType") String accountType);

    @SqlQuery("SELECT ACCOUNT_BALANCE FROM ACCOUNT_INFO WHERE ACCOUNT_IBAN = :accountIban")
    Optional<BigDecimal> getBalance(@Bind("accountIban") String accountIban);

    @SqlUpdate("UPDATE ACCOUNT_INFO SET ACCOUNT_BALANCE = :accountBalance WHERE ACCOUNT_IBAN = :accountIban")
    void updateBalance(@Bind("accountIban") String accountIban, @Bind("accountBalance") BigDecimal accountBalance);

    @SqlQuery("SELECT * FROM ACCOUNT_INFO WHERE ACCOUNT_IBAN = :accountIban")
    @RegisterConstructorMapper(Account.class)
    Optional<Account> getAccount(@Bind("accountIban") String accountIban);
}
