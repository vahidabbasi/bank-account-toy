package com.dkb.validators;

import com.dkb.exceptions.BankAccountException;
import com.dkb.model.Account;
import com.dkb.model.AccountType;
import com.dkb.model.request.TransferMoneyRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validateTransferMoneyRequest(TransferMoneyRequest transferMoneyRequest, Account accountFrom,
                                             Account accountTo) {

        if (transferMoneyRequest.getAccountFromIban().equals(transferMoneyRequest.getAccountToIban())) {
            throw new BankAccountException("Account from iban and account to iban should not be the same", HttpStatus.BAD_REQUEST);
        } else if (accountFrom.getAccountBalance().compareTo(transferMoneyRequest.getAmount()) < 0) {
            throw new BankAccountException("Account with id: " + accountFrom.getAccountIban() +
                    " does not have enough balance to transfer.", HttpStatus.BAD_REQUEST);
        } else if (accountFrom.getAccountType().equals(AccountType.SAVING_ACCOUNT.getAccountTypeCode()) &&
                !accountTo.getAccountType().equals(AccountType.CHECKING_ACCOUNT.getAccountTypeCode())) {
            throw new BankAccountException("Only transferring money from the savings account to the checking account is possible", HttpStatus.BAD_REQUEST);
        } else if (accountFrom.getAccountType().equals(AccountType.PRIVATE_LOAN_ACCOUNT.getAccountTypeCode())) {
            throw new BankAccountException("Withdrawal from private loan account is not possible", HttpStatus.BAD_REQUEST);
        }
    }
}
