package com.dkb.model;

public enum AccountType {
    CHECKING_ACCOUNT("checking_account"),
    SAVING_ACCOUNT("saving_account"),
    PRIVATE_LOAN_ACCOUNT("private_loan_account");

    private final String accountTypeCode;

    AccountType(final String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }
}
