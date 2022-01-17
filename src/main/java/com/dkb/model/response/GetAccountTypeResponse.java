package com.dkb.model.response;

import com.dkb.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountTypeResponse {
    private List<List<Account>> accounts;
}
