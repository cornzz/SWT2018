import {MDCChipSet} from '@material/chips';

[].slice.call(document.querySelectorAll('.mdc-chip-set')).forEach(function(element) {
    console.log("MDCChipSet", element);
    new MDCChipSet(element);
});