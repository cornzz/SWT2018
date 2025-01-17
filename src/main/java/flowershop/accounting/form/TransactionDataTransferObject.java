package flowershop.accounting.form;

import flowershop.accounting.form.validation.IsDouble;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Transfer object for {@link flowershop.order.Transaction} data.
 *
 * @author Cornelius Kummer
 */
public class TransactionDataTransferObject {

	@NotNull(message = "{Dto.amount.NotEmpty}")
	@IsDouble(message = "{Dto.amount.Numeric}")
	private String amount;

	@NotNull(message = "{Dto.description.NotEmpty}")
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
