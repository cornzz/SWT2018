package kickstart.form;

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
	private List<String> flowerIDs;
	private List<String> serviceIDs;

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

	public void setFlowerIDs(List<String> flowerIDs) {
		this.flowerIDs = flowerIDs;
	}

	public List<String> getFlowerIDs() {
		return flowerIDs;
	}

	public void setServiceIDs(List<String> serviceIDs) {
		this.serviceIDs = serviceIDs;
	}

	public List<String> getServiceIDs() {
		return serviceIDs;
	}
}