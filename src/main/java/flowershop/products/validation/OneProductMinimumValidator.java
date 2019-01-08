package flowershop.products.validation;

import flowershop.products.form.CompoundFlowerShopProductTransferObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneProductMinimumValidator implements ConstraintValidator<OneProductMinimum, Object> {

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {

		CompoundFlowerShopProductTransferObject form = (CompoundFlowerShopProductTransferObject) obj;
		return !form.getSelectedFlowerShopItems().isEmpty() || !form.getSelectedFlowerShopServices().isEmpty();

	}
}
