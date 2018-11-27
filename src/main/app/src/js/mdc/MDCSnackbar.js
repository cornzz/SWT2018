import {MDCSnackbar} from '@material/snackbar';

const snackbarElement = document.querySelector('.mdc-snackbar');

if (snackbarElement) {
    const snackbar = new MDCSnackbar(snackbarElement);
}