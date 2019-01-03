package flowershop.products.initializer;

import flowershop.products.*;
import flowershop.products.form.PlaceholderImage;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(30)
public class CompoundFlowerShopProductCatalogInitializer implements DataInitializer {

	private final CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog;
	private final FlowerShopItemCatalog flowerShopItemCatalog;
	private final FlowerShopServiceCatalog flowerShopServiceCatalog;

	CompoundFlowerShopProductCatalogInitializer(CompoundFlowerShopProductCatalog compoundFlowerShopProductCatalog, FlowerShopItemCatalog flowerShopItemCatalog, FlowerShopServiceCatalog flowerShopServiceCatalog) {
		Assert.notNull(compoundFlowerShopProductCatalog, "CompoundFlowerShopProductCatalog must not be null!");
		Assert.notNull(flowerShopItemCatalog, "FlowerShopItemCatalog must not be null!");
		Assert.notNull(flowerShopServiceCatalog, "FlowerShopServiceCatalog must not be null!");

		this.compoundFlowerShopProductCatalog = compoundFlowerShopProductCatalog;
		this.flowerShopItemCatalog = flowerShopItemCatalog;
		this.flowerShopServiceCatalog = flowerShopServiceCatalog;
	}

	@Override
	public void initialize() {
		if (compoundFlowerShopProductCatalog.findAll().iterator().hasNext()) {
			return;
		}

		// TODO: conversion to util package

		// convert list to iterable
		Iterable<FlowerShopItem> flowersIterable = flowerShopItemCatalog.findAll();
		List<FlowerShopItem> flowerShopItems = new ArrayList<>();
		flowersIterable.forEach(flowerShopItems::add);

		// convert list to iterable
		Iterable<FlowerShopService> serviceIterable = flowerShopServiceCatalog.findAll();
		List<FlowerShopService> flowerShopServices = new ArrayList<>();
		serviceIterable.forEach(flowerShopServices::add);

		// placeholder image
		String image = PlaceholderImage.FOUR_HUNDRED_BY_FOUR_HUNDRED.getImage();

		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct("RGB", "Very nice bouquet!", flowerShopItems, flowerShopServices, image));
		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct("RBG", "Very nice bouquet!", flowerShopItems, flowerShopServices, image));
		compoundFlowerShopProductCatalog.save(new CompoundFlowerShopProduct("BRG", "Very nice bouquet!", flowerShopItems, flowerShopServices, image));
	}
}
