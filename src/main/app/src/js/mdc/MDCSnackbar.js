import {MDCSnackbar} from '@material/snackbar';

const snackbarElement = document.querySelector('.mdc-snackbar');
const snackbarTextElement = document.querySelector('.fs-snackbar-text');
const snackbarActionTextElement = document.querySelector('.mdc-snackbar__action-button');

if (snackbarTextElement) {
    const snackbar = new MDCSnackbar(snackbarElement);

    const dataObj = {
        message: snackbarTextElement.innerHTML,
        timeout: 5000,
        actionText: snackbarActionTextElement.innerHTML,
        actionHandler: function () {
        }
    };

    snackbar.show(dataObj);
}