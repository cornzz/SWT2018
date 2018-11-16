import {MDCDialog} from '@material/dialog';

console.log("MDCDialog");

// const list = new MDCList(document.querySelector('.mdc-dialog .mdc-list'));

const addFlowerDialogElement = document.getElementById('fp-add-flowers-dialog');
const addServiceDialogElement = document.getElementById('fp-add-services-dialog');

const addFlowerChipElement = document.getElementById('fp-add-flowers-chip');
const addServiceChipElement = document.getElementById('fp-add-services-chip');

if (addFlowerChipElement) {
    let flowerDialog = new MDCDialog(addFlowerDialogElement);

    console.log('MDCDialog:created [flowers]');

    if (addFlowerChipElement) {
        addFlowerChipElement.addEventListener('click', function () {
            flowerDialog.open();
        });
    }

    flowerDialog.listen('MDCDialog:opened', () => {
        // list.layout();
        console.log('MDCDialog:opened [flowers]');
    });
}

if (addServiceChipElement) {
    let serviceDialog = new MDCDialog(addServiceDialogElement);

    console.log('MDCDialog:created [services]');

    if (addServiceChipElement) {
        addServiceChipElement.addEventListener('click', function () {
            serviceDialog.open();
        });
    }

    serviceDialog.listen('MDCDialog:opened', () => {
        // list.layout();
        console.log('MDCDialog:opened [services]');
    });
}


