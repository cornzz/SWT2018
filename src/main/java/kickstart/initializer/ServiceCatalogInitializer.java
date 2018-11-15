package kickstart.initializer;

import kickstart.Service;
import kickstart.ServiceCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
public class ServiceCatalogInitializer implements DataInitializer {

	private final ServiceCatalog serviceCatalog;

	ServiceCatalogInitializer(ServiceCatalog serviceCatalog) {
		Assert.notNull(serviceCatalog, "ServiceCatalog must not be null!");

		this.serviceCatalog = serviceCatalog;
	}

	@Override
	public void initialize() {
		if(serviceCatalog.findAll().iterator().hasNext()) {
			return;
		}

		serviceCatalog.save(new Service("Make Bouquet", Money.of(100, "EUR"), "Basic service"));
	}
}
