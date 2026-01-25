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
        console.log("add csrf")
        if (token && header) {
            e.detail.headers[header] = token;
            console.log("add csrf - done")
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

        // Sort rows
        rows.sort((a, b) => {
            let aText = a.cells[colIndex].innerText;
            let bText = b.cells[colIndex].innerText;

            const aNum = parseFloat(aText);
            const bNum = parseFloat(bText);

            if (!isNaN(aNum) && !isNaN(bNum)) {
                return newDirection === "asc" ? aNum - bNum : bNum - aNum;
            } else {
                return newDirection === "asc"
                    ? aText.localeCompare(bText)
                    : bText.localeCompare(aText);
            }
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