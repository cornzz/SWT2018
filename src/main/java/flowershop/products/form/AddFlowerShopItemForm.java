package flowershop.products.form;

import flowershop.accounting.form.validation.IsDouble;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AddFlowerShopItemForm {


	@NotNull
	@NotEmpty(message = "{Dto.name.NotEmpty}")
	private String name;

	@NotNull(message = "{Dto.amount.NotEmpty}")
	@IsDouble(message = "{Dto.amount.Numeric}")
	private String price;

	@NotNull(message = "{Dto.amount.NotEmpty}")
	@IsDouble(message = "{Dto.amount.Numeric}")
	private String amount;

	@NotNull
	@NotEmpty(message = "{Dto.description.NotEmpty}")
	private String description;


	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

}
