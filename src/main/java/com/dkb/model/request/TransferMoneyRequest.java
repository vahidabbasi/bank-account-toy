package com.dkb.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyRequest {
	
	@NotNull
	@ApiModelProperty(required = true)
	private String accountFromIban;

	@NotNull
	@ApiModelProperty(required = true)
	private String accountToIban;

	@NotNull
	@ApiModelProperty(required = true)
	@Min(value = 0, message = "Transfer amount can not be less than zero")
	private BigDecimal amount;
}
