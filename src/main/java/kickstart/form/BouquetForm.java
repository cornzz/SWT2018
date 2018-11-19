package kickstart.form;

import kickstart.Flower;
import kickstart.Service;
import org.salespointframework.catalog.ProductIdentifier;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/*
 *  TODO: use interface instead of POJO
 *  interface BouquetForm {
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

public class BouquetForm {
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	private List<Flower> selectedFlowers;
	private List<Service> selectedServices;

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

	public void setSelectedFlowers(List<Flower> selectedFlowers) {
		this.selectedFlowers = selectedFlowers;
	}

	public List<Flower> getSelectedFlowers() {
		return selectedFlowers;
	}

	public void setSelectedServices(List<Service> selectedServices) {
		this.selectedServices = selectedServices;
	}

	public List<Service> getSelectedServices() {
		return selectedServices;
	}
}