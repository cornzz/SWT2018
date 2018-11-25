import {MDCTopAppBar} from '@material/top-app-bar/index';
import {MDCDrawer} from "@material/drawer/index";

console.log('MDCTopAppBar');

const topAppBarElement = document.querySelector('.mdc-top-app-bar');
const drawerElement = document.querySelector('.mdc-drawer');

if (topAppBarElement && drawerElement) {
    const topAppBar = new MDCTopAppBar(topAppBarElement);
    const drawer = MDCDrawer.attachTo(drawerElement);

    topAppBar.setScrollTarget(document.getElementsByTagName('main')[0]);
    topAppBar.listen('MDCTopAppBar:nav', () => {
        drawer.open = !drawer.open;
    });
}