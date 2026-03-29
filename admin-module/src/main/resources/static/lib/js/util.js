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

document.addEventListener("DOMContentLoaded", () => {

    document.body.addEventListener("refreshPage", function (evt) {
        const detail = evt.detail || '';
        const url = detail.url;

        if (url) {
            console.log("refresh with url");
            window.location.href = url;
        } else {
            console.log("refresh without url");
            window.location.reload();

        }
    });

    document.body.addEventListener("closeModal", () => {
        const modal = document.getElementById("datetime-modal")
        closeModal(modal)
        location.reload();
    });

    document.body.addEventListener('htmx:configRequest', e => {
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

    /** every table has a highlight on it **/
    document.querySelectorAll('table:not([data-no-highlight]) tbody tr').forEach(row => {
        row.addEventListener('mouseover', () => row.classList.add('highlight'));
        row.addEventListener('mouseout', () => row.classList.remove('highlight'));
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
