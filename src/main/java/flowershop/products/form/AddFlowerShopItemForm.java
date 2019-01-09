package flowershop.products.form;

import flowershop.accounting.form.validation.IsDouble;
import flowershop.accounting.form.validation.IsLong;
import flowershop.products.FlowerShopItem;
import org.javamoney.moneta.Money;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Transfer object for {@link flowershop.products.FlowerShopItem} data.
 *
 * @author Friedrich Bethke
 */
public class AddFlowerShopItemForm {

	@NotNull(message = "{Dto.name.NotEmpty}")
	@NotEmpty(message = "{Dto.name.NotEmpty}")
	private String name;

	@NotEmpty(message = "{Dto.base.NotEmpty}")
	@IsDouble(message = "{Dto.base.Numeric}")
	private String basePrice;

	@NotEmpty(message = "{Dto.retail.NotEmpty}")
	@IsDouble(message = "{Dto.retail.Numeric}")
	private String retailPrice;

	@NotEmpty(message = "{Dto.amount.NotEmpty}")
	@IsLong(message = "{Dto.amount.Numeric}")
	private String amount;

	@NotNull(message = "{Dto.description.NotEmpty}")
	@NotEmpty(message = "{Dto.description.NotEmpty}")
	private String description;

	public String getName() {
		return name;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public String getRetailPrice() {
		return retailPrice;
	}

	public String getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FlowerShopItem convertToObject() {
		return new FlowerShopItem(
				getName(),
				Money.of(Double.valueOf(getBasePrice()), EURO),
				Money.of(Double.valueOf(getRetailPrice()), EURO),
				getDescription(),
				Integer.valueOf(getAmount())
		);
	}
}