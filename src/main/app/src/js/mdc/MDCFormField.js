import {MDCTextField} from '@material/textfield';
import {MDCFormField} from '@material/form-field';
import {MDCRadio} from '@material/radio';

// TODO: refactor in multiple "packages"

[].slice.call(document.querySelectorAll('.mdc-form-field')).forEach(function(formField) {
    formField.querySelectorAll('.mdc-text-field').forEach(function(textField) {
        formField.input = new MDCTextField(textField);
    }.bind(formField));
    formField.querySelectorAll('.mdc-radio').forEach(function (radio) {
        formField.input = radio;
    }.bind(formField));

    // TODO: one form field -> one input -> join arrays first
});
