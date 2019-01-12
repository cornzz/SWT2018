package flowershop.products.form;

import flowershop.products.CompoundFlowerShopProduct;
import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopService;
import flowershop.products.validation.OneProductMinimum;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;

/**
 * Transfer object for {@link CompoundFlowerShopProduct}.
 *
 * @author Jonas Knobloch
 */
@OneProductMinimum
public class CompoundFlowerShopProductTransferObject {

	private ProductIdentifier id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	private List<FlowerShopItem> selectedFlowerShopItems = new ArrayList<>();
	private List<FlowerShopService> selectedFlowerShopServices = new ArrayList<>();

	private Map<FlowerShopItem, Quantity> quantities = new HashMap<>();

	private MultipartFile image;
	private String imageBase64;

	public ProductIdentifier getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<FlowerShopItem> getSelectedFlowerShopItems() {
		return selectedFlowerShopItems;
	}

	public List<FlowerShopService> getSelectedFlowerShopServices() {
		return selectedFlowerShopServices;
	}

	public Map<FlowerShopItem, Quantity> getQuantities() {
		return quantities;
	}

	public MultipartFile getImage() {

		// Pattern pattern = Pattern.compile("data:(?<type>image/png|image/jpg);base64,(?<content>[a-zA-Z0-9+/=]+)");
		// Matcher matcher = pattern.matcher(getImageBase64());

		// TODO: convert base64 string back to to file (create MultipartFile from FileItem -> use org.apache.commons.fileupload)
		// currently not required -> leaving this blank for now

		return null;
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public HashMap<FlowerShopItem, Quantity> getSelectedFlowerShopItemsWithQuantities() {

		HashMap<FlowerShopItem, Quantity> selectedFlowerShopItemsWithQuantities = new HashMap<>();

		getSelectedFlowerShopItems().forEach(item -> selectedFlowerShopItemsWithQuantities.put(item, getQuantities().get(item)));

		selectedFlowerShopItemsWithQuantities.values().removeIf(Objects::isNull);

		// selectedFlowerShopItemsWithQuantities.forEach((key, value) -> System.out.println("FOO: Selected Item: " + key.getId() + " Quantity: " + value));

		return selectedFlowerShopItemsWithQuantities;
	}

	public Money getPrice() {
		return CompoundFlowerShopProduct.calcPrice(getSelectedFlowerShopItemsWithQuantities(), getSelectedFlowerShopServices());
	}

	public void setId(ProductIdentifier id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSelectedFlowerShopItems(List<FlowerShopItem> selectedFlowerShopItems) {
		this.selectedFlowerShopItems = selectedFlowerShopItems;
	}

	public void setSelectedFlowerShopServices(List<FlowerShopService> selectedFlowerShopServices) {
		this.selectedFlowerShopServices = selectedFlowerShopServices;
	}

	public void setQuantities(Map<FlowerShopItem, Quantity> quantities) {
		this.quantities = quantities;
	}

	public void setImage(MultipartFile image) throws IOException {

		if (image.getContentType() == null || !image.getContentType().equals("image/png") && !image.getContentType().equals("image/jpeg")) {

			// no id set -> new product
			if (getId() == null) {
				setImageBase64(PlaceholderImage.FOUR_HUNDRED_BY_FOUR_HUNDRED.getImage());
			}

			// we do not store the actual file so we have to stop here since this is an edit without a new image

			return;
		}

		String type = image.getContentType().split("/")[1];

		this.image = image;
		this.imageBase64 = "data:image/" + type + ";base64," + Base64.getEncoder().encodeToString(image.getBytes());
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

	public CompoundFlowerShopProduct convertToObject() {

		return new CompoundFlowerShopProduct(
				getName(),
				getDescription(),
				getSelectedFlowerShopItemsWithQuantities(),
				getSelectedFlowerShopServices(),
				getImageBase64()
		);
	}
}
