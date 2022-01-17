package com.dkb.model.response;

import com.dkb.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountTransactionsResponse {
    private List<Transaction> transactions;
}
