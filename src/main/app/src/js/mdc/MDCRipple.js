import {MDCRipple} from '@material/ripple/index';

console.log('MDCRipple');

// const ripple = new MDCRipple(document.querySelector('.mdc-button'));
[].slice.call(document.querySelectorAll('.mdc-button')).forEach(function(element) {
    new MDCRipple(element)
});

// const iconButtonRipple = new MDCRipple(document.querySelector('.mdc-icon-button'));
// iconButtonRipple.unbounded = true;
[].slice.call(document.querySelectorAll('.mdc-icon-button')).forEach(function(element) {
    const ripple = new MDCRipple(element);
    ripple.unbounded = true;
});

[].slice.call(document.querySelectorAll('.mdc-chip')).forEach(function(element) {
    new MDCRipple(element);
});

[].slice.call(document.querySelectorAll('.mdc-card__primary-action')).forEach(function(element) {
    new MDCRipple(element);
});

const ripples = [
    document.querySelector('.mdc-fab')
];

ripples.forEach(function (element) {
   if(element) {
       new MDCRipple(element);
   }
});