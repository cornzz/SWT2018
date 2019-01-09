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

import static flowershop.order.SubTransaction.SubTransactionType.REORDER;

/**
 * An extension of {@link Order} to add flower shop specific methods and values.
 *
 * @author Friedrich Bethke
 */
@Entity
public class Transaction extends Order {
	public enum TransactionType {
		ORDER,
		COLLECTION,
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

	public void addSubTransaction(String item, Quantity quantity, MonetaryAmount price, SubTransaction.SubTransactionType type) {
		SubTransaction subTransaction = new SubTransaction(item, quantity, price, type);
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

	public Quantity getQuantity() {
		return getSubTransactions().stream().filter(subTransaction -> subTransaction.isType(REORDER))
				.map(SubTransaction::getQuantity).reduce(Quantity.of(0), Quantity::add);
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public void setPrice(MonetaryAmount price) {
		this.price = price;
	}

	public void setItemId(InventoryItemIdentifier itemId) {
		this.itemId = itemId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isType(TransactionType type) {
		return this.type.equals(type);
	}

	public void setOptional(InventoryItemIdentifier itemId, MonetaryAmount price, String description) {
		this.itemId = itemId;
		this.price = price;
		this.description = description;
	}

}
