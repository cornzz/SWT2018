package flowershop.products.form;

import flowershop.products.FlowerShopItem;
import flowershop.products.FlowerShopService;
import flowershop.products.validation.OneProductMinimum;
import org.salespointframework.quantity.Quantity;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.io.IOException;
import java.util.Base64;
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
			this.imageBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQBAMAAABykSv/AAAAG1BMVEXMzMyWlpacnJyqqqrFxcWxsbG" +
					"jo6O3t7e+vr6He3KoAAAACXBIWXMAAA7EAAAOxAGVKw4bAAAEpklEQVR4nO3ZTU/bShSH8WPndekJJbB0Sq8uS9LbSl06gLpOWHS" +
					"dSEh0GbgS69B+8TtnZsJMaA1dtHeC9Pwk7BMcpPO3PeMXRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
					"AAAAAAAAAAAAAAAAAAP4fw/ejT0+r532fnGyeVjn1jVstjDHzJ1WLVaXLnv3aoexWWd26INqM7yZWLQbGBdG8PnCscuoaF+RBmzG" +
					"bnarFzAUZuq8d71RZdXyQifnQnZp6p2oxdUFKc9BcmNFOldWDCzLUk6mnuzVWLewBqNzfzfWk2qRVVhMXpGOWdrk6SKsWpQ8yHYt" +
					"OFGdplVPPLDRI4YfHKK1aLA5dkDA86rTKaTYqNIhvvDBNUv1c19QaJDRu3iRVVqtjF+TWnUp9s04qZee0xq4m8QD1zUaDDPyptDp" +
					"KqpxsFy7INFxC5knlLLQYmNilDapBOuEScphUOZ2bxgXx+1N3bqycUk+ZMg6A7uSNCxIOmY0Vq5ymR+KCTNx0O7QzVqycgTnYmVs" +
					"7tm0NUvpfPYyTKiMdqDtB6qTyVqbpmtikzgVpkFFSZaRN+CBuzulqkMfKezBnvWRG0pw+SKMfZ6OkymhxID6ICe1XSeV1zPEsXux" +
					"6Wuq2MD/PTFLl09WufRDftwZ5rLbfGU/jRWWmpQ/iPhcmqfJxE84LR0Rv0uPM6ua0/TsibqZ5YYzovdVj7Sez/RsjboZ6YdayE3C" +
					"4yst2rtq/WcsE4+eCDE0cIsX2L5b7GmR7PV8mVdA3cdKKQbbX87HEKp8YZHuHdZZUwW3yGBuDbO+wDiRW+cQgbXe/1mQcL+wxyL7" +
					"d/UoY7NunkE1SefapdxFTOenzyHFSZVYkT4gmrbxzs5w9efpLnxCrtMqreP6Z3d759p685trLZ/YQpPUtirvznew++O7nW5Rwk9T" +
					"2Xqujtyy3u+8R9/O9VgjS9qbxQU+ZcvfNwn6+aQxBOo9vfGOlVnpWDc3ORcKP6+njG99YZVU89zZ+4COsdkaA2c+38cHr//8IAAA" +
					"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOC1K8LPq/fKgpjRh5YtT4JU9lNhjJGRSFnpR+ticiP" +
					"y2VxLUYtM/mynL6i6/7Rs+UkQtxxv5D4E6f7VfLeLy6um+CqD1Z/v9hmVnMv7EymbgS16b8+kuKp01Xm7sFuLqxPpr+2WJMjRUm5" +
					"CkM7cL3pnxb30b3Pm0CC968/zznom/8qXy49SXLvVl0vdwYXdNKzlYxqkOh3WIUjZ+EW3LsrNt7wDyp5aZdOth2dX60/yzu77onG" +
					"rdzKzWwu7SW7sjx1MOj5ckPv+OgQptouqGCxv8gaxg93t6Pp0WWuz2peuqu0YqeRbZy3pESnv5McjIn/XmY+IdqK7ffl1aQ+D61Z" +
					"XyREpr8IXQ5Deofw4RmQxzx5Ex4hcre/mct9caLe6imNEBkfhiyGI++CWyawluS862pCdteShmTUymJxqO7qKs5YM6vDFNIg//S7" +
					"M9joiuYP8gv46dwe/yV3uBn6T4jR3BwCAX/AfHWrCE1mxqeoAAAAASUVORK5CYII=";

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