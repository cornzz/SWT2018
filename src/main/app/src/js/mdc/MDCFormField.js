import {MDCTextField} from '@material/textfield';
import {MDCFormField} from '@material/form-field';
import {MDCTextFieldHelperText} from '@material/textfield/helper-text';

// TODO: refactor in multiple "packages"

[].slice.call(document.querySelectorAll('.mdc-form-field')).forEach(function(formField) {
    [].slice.call(formField.querySelectorAll('.mdc-text-field')).forEach(function(textField) {
        formField.input = new MDCTextField(textField);
    }.bind(formField));

});

[].slice.call(document.querySelectorAll('.mdc-text-field-helper-text')).forEach(function (helperText) {
    new MDCTextFieldHelperText(helperText);
});