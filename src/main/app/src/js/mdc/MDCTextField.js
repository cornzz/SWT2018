import {MDCTextField} from '@material/textfield';
import {MDCTextFieldHelperText} from '@material/textfield/helper-text';
import {MDCCheckbox} from '@material/checkbox';

let flowerDialogTextFields = [];
let flowerDialogCheckboxes = [];

[].slice.call(document.querySelectorAll('.mdc-text-field')).forEach(function (textFieldElement) {
    const textField = new MDCTextField(textFieldElement);

    if (textFieldElement.classList.contains('fs-choose-flower-dialog__quantity-input')) {
        flowerDialogTextFields.push(textField);
    }

});

[].slice.call(document.querySelectorAll('.mdc-text-field-helper-text')).forEach(function (helperText) {
    new MDCTextFieldHelperText(helperText);
});

[].slice.call(document.querySelectorAll('.mdc-checkbox')).forEach(function (checkboxElement) {

    const checkbox = new MDCCheckbox(checkboxElement);

    if (checkboxElement.classList.contains('fs-choose-flower-dialog__checkbox')) {
        flowerDialogCheckboxes.push(checkbox);

        checkboxElement.getElementsByTagName('input')[0].addEventListener('click', function () {

            let index = flowerDialogCheckboxes.indexOf(checkbox);

            if (index !== -1 && flowerDialogTextFields.size === flowerDialogCheckboxes.size) {

                const textField = flowerDialogTextFields[index];

                textField.disabled = !checkbox.checked;

                if (checkbox.checked) {
                    try {
                        textField.focus(); // focus not a function?
                    } catch (err) {
                        textField.input_.focus(); // fuck off
                    }
                } else {
                    textField.value = null;
                }
            }
        });
    }
});