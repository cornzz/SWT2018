package flowershop.order;

import org.salespointframework.core.AbstractEntity;
import org.salespointframework.quantity.Quantity;
import org.springframework.lang.NonNull;

import javax.money.MonetaryAmount;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;


@Entity

public class SubTransaction extends AbstractEntity<SubTransactionIdentifier> {
	public enum SubTransactionType {
		DEFICIT,
		REORDER
	}

	private SubTransaction() {
	}

	private @NonNull
	Quantity quantity;
	private @NonNull
	LocalDateTime date;
	private @NonNull
	MonetaryAmount price;
	private @NonNull
	String flower;
	private @NonNull
	Boolean status = true;
	private @NonNull
	SubTransactionType type;

	@EmbeddedId
	@AttributeOverride(name = "id", column = @Column(name = "SUBTRANSACTION_ID")) //
	private SubTransactionIdentifier subTransactionIdentifier = new SubTransactionIdentifier();


	public SubTransaction(Quantity quantity, MonetaryAmount price, String flower, SubTransactionType type) {
		this.quantity = quantity;
		this.date = LocalDateTime.now();
		this.price = price;
		this.flower = flower;
		this.type = type;

	}

	public void setStatus(Boolean status) {
		this.status = false;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public MonetaryAmount getPrice() {
		return price;
	}

	public String getFlower() {
		return flower;
	}

	public SubTransactionIdentifier getId() {
		return subTransactionIdentifier;
	}

	public Boolean getStatus() {
		return status;
	}

	public SubTransactionType getType() {
		return type;
	}
}
