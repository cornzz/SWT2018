package flowershop.order;

import org.salespointframework.core.SalespointIdentifier;

import javax.persistence.Embeddable;

@Embeddable
class SubTransactionIdentifier extends SalespointIdentifier {

	private static final long serialVersionUID = 2953538683490057901L;

	SubTransactionIdentifier() {
		super();
	}

	SubTransactionIdentifier(String SubTransactionIdentifier) {
		super(SubTransactionIdentifier);
	}
}