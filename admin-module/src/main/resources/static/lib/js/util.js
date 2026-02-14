
function toggleAllCheckBoxesInSurroundingElement(masterCheckbox, surroundingElement) {

    const fieldset = masterCheckbox.closest(surroundingElement);
    if (!fieldset) return;

    const checkboxes = fieldset.querySelectorAll('input[type="checkbox"]');

    checkboxes.forEach(function (checkbox) {
        if (checkbox !== masterCheckbox) {
            checkbox.checked = masterCheckbox.checked;
        }
    });
}


/** Global vars **/
const STORAGE_KEY = 'pico-theme';
const DEFAULT_THEME = 'grey';
const BASE_PATH = '/lib/pico/pico.';
const POST_FIX = '.min.css';


function applyTheme(color) {

    let link = document.getElementById('pico-theme');

    if (!link) {
        link = document.createElement('link');
        link.id = 'pico-theme';
        link.rel = 'stylesheet';

        const customCss = document.querySelector('link[href*="style.css"]');
        document.head.insertBefore(link, customCss);
    }

    link.href = BASE_PATH + color + POST_FIX;
    localStorage.setItem(STORAGE_KEY, color);

    // remove aria-current from all
    document.querySelectorAll('[data-color]').forEach(el => el.removeAttribute('aria-current'));

    // set aria-current on selected
    const active = document.querySelector(`[data-color="${color}"]`);
    if (active) active.setAttribute('aria-current', 'page');
}
applyTheme(localStorage.getItem(STORAGE_KEY) || DEFAULT_THEME);



document.addEventListener("DOMContentLoaded", () => {

    document.body.addEventListener("refreshPage", () => {
        location.reload();
    });

    document.body.addEventListener("closeModal", () => {
        const modal = document.getElementById("datetime-modal")
        closeModal(modal)
        location.reload();
    });

    document.addEventListener('htmx:configRequest', e => {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        if (token && header) {
            e.detail.headers[header] = token;
        }
    });
    document.body.addEventListener("htmx:responseError", function (evt) {
        const status = evt.detail.xhr.status;

        if (status === 401 || status === 403) {
            window.location.href = "/admin/login";
        }
    });

    // todo this makes the tool tip blink but it doesnt go away.
    // document.querySelectorAll('[data-tooltip]').forEach(el => {
    //     el.addEventListener('mouseenter', () => {
    //         const tooltip = el.dataset.tooltip;
    //         setTimeout(() => {
    //             el.dataset.tooltip = '';
    //             setTimeout(() => el.dataset.tooltip = tooltip, 100);
    //         }, 1000);
    //     });
    // });
});
