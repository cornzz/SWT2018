package kickstart.form;

import kickstart.FlowerShopItem;
import kickstart.FlowerShopService;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/*
 *  TODO: use interface instead of POJO
 *  interface AddCompoundFlowerShopProductForm {
 *
 * 	    @NotEmpty
 * 	    String name();
 *
 * 	    @NotEmpty
 * 	    String description();
 * 	    // List<String> flowers;
 * 	    // List<String> services;
 *  }
 */

public class AddCompoundFlowerShopProductForm {
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	private List<FlowerShopItem> selectedFlowerShopItems;
	private List<FlowerShopService> selectedFlowerShopServices;

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setSelectedFlowerShopItems(List<FlowerShopItem> selectedFlowerShopItems) {
		this.selectedFlowerShopItems = selectedFlowerShopItems;
	}

	public List<FlowerShopItem> getSelectedFlowerShopItems() {
		return selectedFlowerShopItems;
	}

	public void setSelectedFlowerShopServices(List<FlowerShopService> selectedFlowerShopServices) {
		this.selectedFlowerShopServices = selectedFlowerShopServices;
	}

	public List<FlowerShopService> getSelectedFlowerShopServices() {
		return selectedFlowerShopServices;
	}
}