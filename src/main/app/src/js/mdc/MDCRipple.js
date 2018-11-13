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

const ripple = new MDCRipple(document.querySelector('.mdc-card__primary-action'));
