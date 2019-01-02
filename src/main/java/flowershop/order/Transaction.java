package flowershop.order;

import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Transaction extends Order {
	public enum TransactionType {
		ORDER,
		COLLECTION,
		DONE,
		CUSTOM
	}

	private Transaction() {
	}

	private TransactionType type;
	private MonetaryAmount price;
	private InventoryItemIdentifier itemId;
	private String description;

	@OneToMany(cascade = CascadeType.ALL)
	private List<SubTransaction> subTransactions = new ArrayList<>();

	public Transaction(UserAccount userAccount, PaymentMethod paymentMethod, TransactionType type) {
		super(userAccount, paymentMethod);

		this.type = type;
		this.price = null;
		this.itemId = null;
	}

	public void addSubTransaction(SubTransaction subTransaction) {
		this.subTransactions.add(subTransaction);
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

	public InventoryItemIdentifier getItemId() {
		return itemId;
	}

	public String getDescription() {
		return description;
	}

	public List<SubTransaction> getSubTransactions() {
		return subTransactions;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public void setPrice(MonetaryAmount price) {
		this.price = price;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOptional(InventoryItemIdentifier itemId, MonetaryAmount price, String description) {
		this.itemId = itemId;
		this.price = price;
		this.description = description;
	}

}
