package flowershop.order;

import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

@Entity
public class Transaction extends Order {
	public enum TransactionType {
		DEFICIT,
		ORDER,
		REORDER,
		DONE
	}

	private Transaction() {
	}

	private MonetaryAmount price;
	private TransactionType type;
	private InventoryItemIdentifier flower;
	private String flowerName;
	private Quantity quantity;


	public Transaction(UserAccount userAccount, PaymentMethod paymentMethod, TransactionType type) {
		super(userAccount, paymentMethod);
		this.price = null;
		this.type = type;
		this.flower = null;
		this.quantity = null;
	}

	@Override
	public MonetaryAmount getTotalPrice() {
		return price;
	}

	public MonetaryAmount getPrice() {
		return price;
	}

	public TransactionType getType() {
		return type;
	}

	public InventoryItemIdentifier getFlower() {
		return flower;
	}

	public String getFlowerName() {
		return flowerName;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setPrice(MonetaryAmount price) {
		this.price = price;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public void setFlower(InventoryItemIdentifier flower) {
		this.flower = flower;
	}

	public void setFlowerName(String flowerName) {
		this.flowerName = flowerName;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

}
