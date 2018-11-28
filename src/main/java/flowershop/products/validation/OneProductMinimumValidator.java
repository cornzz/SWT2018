package flowershop.products.validation;

import flowershop.products.form.AddCompoundFlowerShopProductForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneProductMinimumValidator implements ConstraintValidator<OneProductMinimum, Object> {

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
		AddCompoundFlowerShopProductForm form = (AddCompoundFlowerShopProductForm) obj;
		return !form.getSelectedFlowerShopItems().isEmpty() || !form.getSelectedFlowerShopServices().isEmpty();
	}
}
