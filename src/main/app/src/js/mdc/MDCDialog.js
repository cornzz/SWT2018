import {MDCDialog} from '@material/dialog';
import {MDCList} from '@material/list'

console.log("MDCDialog");

const addFlowerDialogElement = document.getElementById('fs-add-flowers-dialog');
const addServiceDialogElement = document.getElementById('fs-add-services-dialog');

const addFlowersListElement = document.getElementById('fs-add-flowers-list');
const addServicesListElement = document.getElementById('fs-add-services-list');

const addFlowerChipElement = document.getElementById('fs-add-flowers-chip');
const addServiceChipElement = document.getElementById('fs-add-services-chip');

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