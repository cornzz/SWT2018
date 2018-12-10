import {MDCSnackbar} from '@material/snackbar';

const snackbarElement = document.querySelector('.mdc-snackbar');
const snackbarTextElement = document.querySelector('.fs-snackbar-text');

if (snackbarTextElement) {
    const snackbar = new MDCSnackbar(snackbarElement);

    const dataObj = {
        message: snackbarTextElement.innerHTML,
        timeout: 5000,
        actionText: 'Dismiss',
        actionHandler: function () {
        }
    };

    snackbar.show(dataObj);
}