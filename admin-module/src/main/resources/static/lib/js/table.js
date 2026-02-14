function toggleTableColumns(tableId, columns, visible) {
    const table = document.getElementById(tableId);
    if (!table) return;

    columns.forEach(column => {
        const header = table.querySelector(`thead th[data-order="${column}"]`);
        if (!header) return;

        const columnIndex = header.cellIndex;

        table.querySelectorAll("tr").forEach(row => {
            let currentIndex = 0;

            Array.from(row.cells).forEach(cell => {
                const colspan = parseInt(cell.getAttribute("colspan") || "1", 10);

                if (currentIndex <= columnIndex && columnIndex < currentIndex + colspan) {
                    // This cell covers the target column
                    cell.style.display = visible ? "" : "none";
                }

                currentIndex += colspan;
            });
        });
    });
}


function sortingTables() {

    const SORTABLE_SELECTOR = 'table:not(.no-sorting) > thead th:not(.no-sorting)';

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


document.addEventListener("DOMContentLoaded", () => {
    sortingTables()
})
