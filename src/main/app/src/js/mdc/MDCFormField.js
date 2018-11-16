import {MDCTextField} from '@material/textfield';
import {MDCFormField} from '@material/form-field';

// TODO: refactor in multiple "packages"

[].slice.call(document.querySelectorAll('.mdc-form-field')).forEach(function(formField) {
    formField.querySelectorAll('.mdc-text-field').forEach(function(textField) {
        formField.input = new MDCTextField(textField);
    }.bind(formField));
});
