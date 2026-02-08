



function applyTheme(color) {
    const STORAGE_KEY = 'pico-theme';
    const DEFAULT_THEME = 'grey';
    const BASE_PATH = '/lib/pico/pico.';
    const POST_FIX = '.min.css';

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


document.addEventListener('DOMContentLoaded', () => {
    // initialize theme **after DOM is ready**
    applyTheme(localStorage.getItem(STORAGE_KEY) || DEFAULT_THEME);
})


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
    sortingTables();
});


function sortingTables() {

    const SORTABLE_SELECTOR = 'table:not(.no-sorting) > thead th';

    const style = document.createElement("style");
    style.textContent = `
  ${SORTABLE_SELECTOR} {
    cursor: pointer;
    user-select: none;
    position: relative;      /* needed for absolute arrow */
    padding-right: 1.5em;    /* space for arrow */
    white-space: nowrap;
  }

  ${SORTABLE_SELECTOR}.asc::after,
  ${SORTABLE_SELECTOR}.desc::after {
    position: absolute;
    right: 0.3em;           /* position arrow at the right edge */
    top: 50%;
    transform: translateY(-50%);
    content: "";
  }

  ${SORTABLE_SELECTOR}.asc::after { content: "▲"; }
  ${SORTABLE_SELECTOR}.desc::after { content: "▼"; }
`;
    document.head.appendChild(style);

// Make all table headers sortable
    document.querySelectorAll(SORTABLE_SELECTOR).forEach(th => {
        th.addEventListener("click", () => sortColumn(th));
    });

    // Function to sort a column
    function sortColumn(th) {
        const table = th.closest("table");
        const tbody = table.tBodies[0];
        const rows = Array.from(tbody.rows);
        const colIndex = Array.from(th.parentNode.children).indexOf(th);

        // Determine sort direction
        const currentDirection = th.classList.contains("asc") ? "asc" : "desc";
        const newDirection = currentDirection === "asc" ? "desc" : "asc";

        // Remove sort classes from all headers in this table
        table.querySelectorAll("th").forEach(header => header.classList.remove("asc", "desc"));

        // Add new sort class to clicked header
        th.classList.add(newDirection);

        rows.sort((a, b) => {
            // get tds, then validate on data-sort-value else value of td
            const aCell = a.cells[colIndex];
            const bCell = b.cells[colIndex];
            const aText = aCell.dataset.sortValue ?? aCell.innerText.trim();
            const bText = bCell.dataset.sortValue ?? bCell.innerText.trim();

            // see if we have numbers
            const aNum = parseFloat(aText);
            const bNum = parseFloat(bText);

            if (!isNaN(aNum) && !isNaN(bNum)) {
                return newDirection === "asc"
                    ? aNum - bNum
                    : bNum - aNum;
            }

            return newDirection === "asc"
                ? aText.localeCompare(bText)
                : bText.localeCompare(aText);
        });

        // Re-append sorted rows
        rows.forEach(row => tbody.appendChild(row));
    }

    document.querySelectorAll("table").forEach(table => {
        const firstTh = table.querySelector("th");
        if (firstTh) sortColumn(firstTh);
    });
}


function filterColumn(th) {
    if (!th.classList.contains("filtering")) return;

    // ALT + click opens filter (normal click keeps sorting working)
    if (!e.altKey) return;

    e.stopPropagation();
    e.preventDefault();

    // already open? toggle
    if (th.querySelector("select")) {
        th.innerHTML = th.dataset.title;
        return;
    }

    const table = th.closest("table");
    const colIndex = Array.from(th.parentNode.children).indexOf(th);
    const rows = Array.from(table.tBodies[0].rows);

    th.dataset.title = th.innerText;

    // Collect distinct values lazily
    const values = [...new Set(
        rows.map(r => r.cells[colIndex].innerText.trim())
    )].sort();

    // Build dropdown
    const select = document.createElement("select");
    select.multiple = true;
    select.size = Math.min(8, values.length);
    select.style.width = "100%";

    values.forEach(v => {
        const opt = document.createElement("option");
        opt.value = v;
        opt.textContent = v || "(empty)";
        select.appendChild(opt);
    });

    th.innerHTML = "";
    th.appendChild(select);

    // Filtering behavior
    select.addEventListener("change", () => {
        const selected = new Set([...select.selectedOptions].map(o => o.value));

        rows.forEach(row => {
            const cell = row.cells[colIndex].innerText.trim();
            row.style.display =
                selected.size === 0 || selected.has(cell) ? "" : "none";
        });
    });
}