import {MDCTextField} from '@material/textfield';
import {MDCTextFieldHelperText} from '@material/textfield/helper-text';


[].slice.call(document.querySelectorAll('.mdc-text-field')).forEach(function (textField) {
    new MDCTextField(textField);
});

[].slice.call(document.querySelectorAll('.mdc-text-field-helper-text')).forEach(function (helperText) {
    new MDCTextFieldHelperText(helperText);
});