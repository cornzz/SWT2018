/*
 *  material components for the web are imported using webpack
 *  if you want to import them here you could do the following:
 *  const MDCRipple = require('./js/mdc/MDCRipple');
 */

let secondaryTable = document.querySelector("#fs-secondaryTable");
let primaryTable = document.querySelector("#fs-primaryTable");

if (primaryTable && secondaryTable) {

    if (primaryTable.offsetWidth < secondaryTable.offsetWidth) {
        primaryTable.style.width = secondaryTable.offsetWidth;
    } else {
        secondaryTable.style.width = primaryTable.offsetWidth;
    }
}