package com.dkb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    private String id;
    private String accountNumber;
    private String accountIban;
    private BigDecimal accountBalance;
    private String accountType;
}