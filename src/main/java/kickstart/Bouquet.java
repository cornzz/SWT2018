package kickstart;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Bouquet extends Product {
	private String description;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Flower> flowers;

	// TODO: change cascade
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Service> services;

	@SuppressWarnings("unused")
	private Bouquet() {}

	public Bouquet(String name, String description, List<Flower> flowers, List<Service> services) {
		super(name, Bouquet.calcPrice(flowers, services));

		this.description = description;
		this.flowers = flowers;
		this.services = services;
	}

	private static Money calcPrice(Iterable<Flower> flowers, Iterable<Service> services) {
		// TODO: calc price
		return Money.of(300, "EUR");
	}

	public String getDescription() {
		return description;
	}

	public Iterable<Flower> getFlowers() {
		return flowers;
	}

	public Iterable<Service> getServices() {
		return services;
	}
}
