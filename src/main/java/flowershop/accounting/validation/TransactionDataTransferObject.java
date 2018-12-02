package flowershop.accounting.validation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TransactionDataTransferObject {

	@NotNull(message = "{Dto.amount.NotEmpty}")
	@IsDouble(message = "{Dto.amount.Numeric}")
	private String amount;

	@NotNull
	@NotEmpty(message = "{Dto.description.NotEmpty}")
	private String description;

	public String getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
