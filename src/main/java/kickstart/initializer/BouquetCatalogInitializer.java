package kickstart.initializer;

import kickstart.*;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(30)
public class BouquetCatalogInitializer implements DataInitializer {

	private final BouquetCatalog bouquetCatalog;
	private final FlowerCatalog flowerCatalog;
	private final ServiceCatalog serviceCatalog;

	BouquetCatalogInitializer(BouquetCatalog bouquetCatalog, FlowerCatalog flowerCatalog, ServiceCatalog serviceCatalog) {
		Assert.notNull(bouquetCatalog, "BouquetCatalog must not be null!");
		Assert.notNull(flowerCatalog, "FlowerCatalog must not be null!");
		Assert.notNull(serviceCatalog, "ServiceCatalog must not be null!");

		this.bouquetCatalog = bouquetCatalog;
		this.flowerCatalog = flowerCatalog;
		this.serviceCatalog = serviceCatalog;
	}

	@Override
	public void initialize() {
		if (bouquetCatalog.findAll().iterator().hasNext()) {
			return;
		}

		// TODO: conversion to util package

		// convert list to iterable
		Iterable<Flower> flowersIterable = flowerCatalog.findAll();
		List<Flower> flowers = new ArrayList<>();
		flowersIterable.forEach(flowers::add);

		// convert list to iterable
		Iterable<Service> serviceIterable = serviceCatalog.findAll();
		List<Service> services = new ArrayList<>();
		serviceIterable.forEach(services::add);

		bouquetCatalog.save(new Bouquet("RGB", "Very nice bouquet!", flowers, services));
		bouquetCatalog.save(new Bouquet("RBG", "Very nice bouquet!", flowers, services));
		bouquetCatalog.save(new Bouquet("BRG", "Very nice bouquet!", flowers, services));
	}
}
