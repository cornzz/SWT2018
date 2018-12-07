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
		DONE,
		CUSTOM
	}

	private Transaction() {
	}

	private TransactionType type;
	private MonetaryAmount price;
	private InventoryItemIdentifier item;
	private String itemName, description;
	private Quantity quantity;


	public Transaction(UserAccount userAccount, PaymentMethod paymentMethod, TransactionType type) {
		super(userAccount, paymentMethod);

		this.type = type;
		this.price = null;
		this.item = null;
		this.quantity = null;
	}

	@Override
	public MonetaryAmount getTotalPrice() {
		return super.getTotalPrice().isZero() ? price : super.getTotalPrice();
	}

	public TransactionType getType() {
		return type;
	}

	public MonetaryAmount getPrice() {
		return price;
	}

	public InventoryItemIdentifier getItem() {
		return item;
	}

	public String getItemName() {
		return itemName;
	}

	public String getDescription() {
		return description;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public void setPrice(MonetaryAmount price) {
		this.price = price;
	}

	public void setItem(InventoryItemIdentifier item) {
		this.item = item;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

}
