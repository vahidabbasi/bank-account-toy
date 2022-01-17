package com.dkb.controller;


import com.dkb.model.Account;
import com.dkb.model.AccountType;
import com.dkb.model.Transaction;
import com.dkb.model.request.AddUpBalanceRequest;
import com.dkb.model.request.TransferMoneyRequest;
import com.dkb.model.response.ErrorResponse;
import com.dkb.model.response.GetAccountTransactionsResponse;
import com.dkb.model.response.GetAccountTypeResponse;
import com.dkb.model.response.GetBalanceResponse;
import com.dkb.service.BankAccountService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v1")
@Validated
@Api("A REST-controller to handle various bank account transaction")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        Objects.requireNonNull(bankAccountService, "bankAccountService was null when injected");
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts")
    @ApiOperation(value = "Get all accounts base on their type", response = GetAccountTypeResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Request OK."),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request is missing or have badly formatted fields.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Unexpected error, such as DB-connection problem etc.", response = ErrorResponse.class)
    })
    public GetAccountTypeResponse getAllAccounts(@RequestParam(value = "accountType")
                                                 @ApiParam(value = "The account type", required = true) List<AccountType> accountTypes) {

        List<List<Account>> accounts = bankAccountService.getAllAccounts(accountTypes);

        return GetAccountTypeResponse.builder()
                .accounts(accounts)
                .build();
    }

    @GetMapping("/balance")
    @ApiOperation(value = "Get the balance of a specified bank account", response = GetBalanceResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Request OK."),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request is missing or have badly formatted fields.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "The Iban was not found.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Unexpected error, such as DB-connection problem etc.", response = ErrorResponse.class)
    })
    public GetBalanceResponse getBalance(@RequestParam(value = "accountIban")
                                         @ApiParam(value = "The account iban number", required = true) String accountIban) {

        BigDecimal balance = bankAccountService.getBalance(accountIban);

        return GetBalanceResponse.builder()
                .balance(balance)
                .build();
    }

    @PutMapping("/accounts/account")
    @ApiOperation(value = "Update account balance")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_NO_CONTENT, message = "Account balance updated.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request is missing or have badly formatted fields.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "The Iban was not found.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Unexpected error, such as DB-connection problem etc.", response = ErrorResponse.class)
    })
    public void updateBalanceAccount(@Valid @RequestBody AddUpBalanceRequest addUpBalanceRequest) {

        bankAccountService.updateAccountBalance(addUpBalanceRequest);
    }

    @PostMapping("/accounts/transfer")
    @ApiOperation(value = "Transfer the money between the accounts")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_NO_CONTENT, message = "Transfer has been done.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request is missing or have badly formatted"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAVAILABLE, message = "Server error", response = ErrorResponse.class)
    })
    public void transferMoney(@RequestBody @Valid TransferMoneyRequest transferMoneyRequest) {

        bankAccountService.transferMoney(transferMoneyRequest);
    }

    @GetMapping("/account/transactions")
    @ApiOperation(value = "Get account transactions", response = GetAccountTransactionsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Request OK."),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request is missing or have badly formatted fields.", response = ErrorResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Unexpected error, such as DB-connection problem etc.", response = ErrorResponse.class)
    })
    public GetAccountTransactionsResponse getAccountTransactions(@RequestParam(value = "accountIbanNumber")
                                                                 @ApiParam(value = "The account iban number", required = true) String accountIbanNumber) {

        List<Transaction> transactions = bankAccountService.getAccountTransactions(accountIbanNumber);

        return GetAccountTransactionsResponse.builder()
                .transactions(transactions)
                .build();
    }
}
