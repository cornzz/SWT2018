/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowershop.order;


import java.util.Optional;

import flowershop.catalog.Item;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
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

import static org.salespointframework.core.Currencies.EURO;

/**
 * A Spring MVC controller to manage the {@link Cart}. {@link Cart} instances are held in the session as they're
 * specific to a certain user. That's also why the entire controller is secured by a {@code PreAuthorize} clause.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class OrderController {

	private final OrderManager<Order> orderManager;

	/**
	 * Creates a new {@link OrderController} with the given {@link OrderManager}.
	 * 
	 * @param orderManager must not be {@literal null}.
	 */
	OrderController(OrderManager<Order> orderManager) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;

	}

	/**
	 * Creates a new {@link Cart} instance to be stored in the session (see the class-level {@link SessionAttributes}
	 * annotation).
	 * 
	 * @return a new {@link Cart} instance.
	 */
	@ModelAttribute("cart")
	Cart initializeCart() {
		Cart cart=new Cart();
		Item item = new Item("Tulpe", Money.of(1.50,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item,20);
		Item item2 = new Item("Gänseblümchen", Money.of(10.0,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item2,50);
		Item item3 = new Item("Rosa", Money.of(0.20,EURO), Item.ItemType.BLUME);
		cart.addOrUpdateItem(item3,3);
		Item item4 = new Item("Irgendein Strauss", Money.of(20.0,EURO), Item.ItemType.STRAUSS);
		cart.addOrUpdateItem(item4,1);

		return cart;
	}

	/**
	 * Adds a {@link Item} to the {@link Cart}. Note how the type of the parameter taking the request parameter
	 * {@code pid} is {@link Item}. For all domain types extening {@link AbstractEntity} (directly or indirectly) a tiny
	 * Salespoint extension will directly load the object instance from the database. If the identifier provided is
	 * invalid (invalid format or no {@link Product} with the id found), {@literal null} will be handed into the method.
	 * 
	 * @param 'disc
	 * @param number
	 * @param 'session
	 * @param 'modelMap
	 * @return
	 */
	@PostMapping("/cart")
	String addDisc(@RequestParam("pid") Item item, @RequestParam("number") int number, @ModelAttribute Cart cart) {

		// (｡◕‿◕｡)
		// Das Inputfeld im View ist eigentlich begrenzt, allerdings sollte man immer auch serverseitig validieren
		int amount = number <= 0 || number > 5 ? 1 : number;

		// (｡◕‿◕｡)
		// Wir fügen dem Warenkorb die Disc in entsprechender Anzahl hinzu.
		cart.addOrUpdateItem(item, Quantity.of(amount));

		// (｡◕‿◕｡)
		// Je nachdem ob disc eine DVD oder eine Bluray ist, leiten wir auf die richtige Seite weiter

		/*switch (disc.getType()) {
			case DVD:
				return "redirect:dvds";
			case BLURAY:
			default:
				return "redirect:blurays";
		}*/
		return "asd";
	}

	@GetMapping("/")
	String basket() {
		return "cart";
	}

	/**
	 * Checks out the current state of the {@link Cart}. Using a method parameter of type {@code Optional<UserAccount>}
	 * annotated with {@link LoggedIn} you can access the {@link UserAccount} of the currently logged in user.
	 * 
	 * @param cart will never be {@literal null}.
	 * @param userAccount will never be {@literal null}.
	 * @return
	 */
	@PostMapping("/checkout")
	String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {

			// (｡◕‿◕｡)
			// Mit completeOrder(…) wird der Warenkorb in die Order überführt, diese wird dann bezahlt und abgeschlossen.
			// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
			Order order = new Order(account, Cash.CASH);

			cart.addItemsTo(order);

			orderManager.payOrder(order);
			orderManager.completeOrder(order);

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/cart");
	}

	@GetMapping("/orders")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	String orders(Model model) {

		model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "orders";
	}
}
