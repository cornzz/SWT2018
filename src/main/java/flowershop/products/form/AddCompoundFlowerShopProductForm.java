package flowershop.products.form;

import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopService;
import flowershop.products.validation.OneProductMinimum;
import org.salespointframework.quantity.Quantity;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.*;

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

@OneProductMinimum
public class AddCompoundFlowerShopProductForm {
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;

	private List<FlowerShopItem> selectedFlowerShopItems;
	private List<FlowerShopService> selectedFlowerShopServices;

	private Map<FlowerShopItem, Quantity> quantities = new HashMap<>();

	private MultipartFile image;
	private String imageBase64;

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

	public Map<FlowerShopItem, Quantity> getQuantities() {
		return quantities;
	}

	public void setQuantities(Map<FlowerShopItem, Quantity> quantities) {
		this.quantities = quantities;
	}

	public HashMap<FlowerShopItem, Quantity> getSelectedFlowerShopItemsWithQuantities() {

		HashMap<FlowerShopItem, Quantity> selectedFlowerShopItemsWithQuantities = new HashMap<>();

		selectedFlowerShopItems.forEach(item -> selectedFlowerShopItemsWithQuantities.put(item, getQuantities().get(item)));

		selectedFlowerShopItemsWithQuantities.values().removeIf(Objects::isNull);

		selectedFlowerShopItemsWithQuantities.forEach((key, value) -> System.out.println("Selected Item: " + key.getId() + " Quantity: " + value));

		return selectedFlowerShopItemsWithQuantities;
	}

	public void setImage(MultipartFile image) throws IOException {

		// TODO: add validator (currently optional)

		if (image.getContentType() == null || !image.getContentType().equals("image/png") && !image.getContentType().equals("image/jpeg")) {

			// placeholder image
			this.imageBase64 = PlaceholderImage.FOUR_HUNDRED_BY_FOUR_HUNDRED.getImage();

			return;
		}

		String type = image.getContentType().split("/")[1];

		this.image = image;
		this.imageBase64 = "data:image/"+ type + ";base64," + Base64.getEncoder().encodeToString(image.getBytes());
	}

	public MultipartFile getImage() {
		return image;
	}

	public String getImageBase64() {
		return imageBase64;
	}
}