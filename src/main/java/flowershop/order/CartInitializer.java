package flowershop.order;


import java.util.Optional;

import flowershop.catalog.Disc;
import flowershop.catalog.Item;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.salespointframework.order.Cart;

import javax.money.format.MonetaryAmountFormat;

import static org.salespointframework.core.Currencies.EURO;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartInitializer {


	@ModelAttribute("cart")
	Cart initializeCart() {
		Cart cart=new Cart();
		Item item = new Item("Tulpe", Money.of(1.50,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item,1);
		Item item2 = new Item("Gänseblümchen", Money.of(10.0,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item2,2);
		Item item3 = new Item("Rosa", Money.of(0.20,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item3,4);
		Item item4 = new Item("Irgendein Strauss", Money.of(20.0,EURO), Item.ItemType.STRAUSS);
		cart.addOrUpdateItem(item4,1);
		return cart;
	}
	@GetMapping("/")
	String basket() {
		return "cart";
	}

/*	public String getPriceSum(){
		//int priceSum=0;
		javax.money.MonetaryAmount asd = cart.getPrice();
		//String convertedPriceSum=Integer.toString(priceSum);
		return asd.toString();
	}*/
}
