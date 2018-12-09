package flowershop.products.form;

import javax.validation.constraints.NotEmpty;

public class EditCompoundFlowerShopProductForm {
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
