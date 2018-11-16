import {MDCDialog} from '@material/dialog';
import {MDCList} from '@material/list'

console.log("MDCDialog");

const addFlowerDialogElement = document.getElementById('fp-add-flowers-dialog');
const addServiceDialogElement = document.getElementById('fp-add-services-dialog');

const addFlowersListElement = document.getElementById('fp-add-flowers-list');
const addServicesListElement = document.getElementById('fp-add-services-list');

const addFlowerChipElement = document.getElementById('fp-add-flowers-chip');
const addServiceChipElement = document.getElementById('fp-add-services-chip');

generateDialogLogic(addFlowerDialogElement, addFlowersListElement, addFlowerChipElement);
generateDialogLogic(addServiceDialogElement, addServicesListElement, addServiceChipElement);

function generateDialogLogic(dialogElement, listElement, chipElement) {
    if (chipElement) {
        let dialog = new MDCDialog(dialogElement);

        console.log('MDCDialog:created');

        if (chipElement) {
            chipElement.addEventListener('click', function () {
                dialog.open();
            });
        }

        if (listElement) {
            const list = new MDCList(listElement);

            dialog.listen('MDCDialog:opened', () => {
                list.layout();
                console.log('MDCDialog:opened');
            });
        }
    }
}