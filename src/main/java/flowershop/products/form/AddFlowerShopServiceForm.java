package flowershop.products.form;

import flowershop.accounting.form.validation.IsDouble;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AddFlowerShopServiceForm {

	@NotNull(message = "{Dto.name.NotEmpty}")
	@NotEmpty(message = "{Dto.name.NotEmpty}")
	private String name;

	@NotEmpty(message = "{Dto.price.NotEmpty}")
	@IsDouble(message = "{Dto.price.Numeric}")
	private String price;

	private String description;

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(String price) {
		this.price = price;
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

	public String getDescription() {
		return description;
	}

}