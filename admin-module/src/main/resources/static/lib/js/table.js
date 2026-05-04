(function () {
    'use strict'

    //
    //
    //
    function enableTableMultiSelect(tableElement) {
        let isMouseDown = false;
        let anchorIndex = null;
        let lastHoveredIndex = null;
        let currentHighlight = [];

        let startX = 0;
        let startY = 0;
        let dragActivated = false;

        function getRows() {
            return Array.from(tableElement.querySelectorAll("tbody tr"));
        }

        function highlightRange(start, end) {
            const rows = getRows();
            const [min, max] = [start, end].sort((a, b) => a - b);
            currentHighlight.forEach(row => row.classList.remove('multiSelected'));
            currentHighlight = [];

            for (let i = min; i <= max; i++) {
                const row = rows[i];
                row.classList.add('multiSelected');
                currentHighlight.push(row);
            }
        }

        function commitSelection() {
            if (currentHighlight.length === 0) return;

            const allChecked = currentHighlight.every(row => {
                const checkbox = row.querySelector('input[type="checkbox"]');
                return checkbox.checked;
            });

            currentHighlight.forEach(row => {
                const checkbox = row.querySelector('input[type="checkbox"]');
                checkbox.checked = !allChecked;
                row.classList.remove('multiSelected');
            });

            currentHighlight = [];
        }

        tableElement.addEventListener('mousedown', (e) => {
            if (e.target.matches('input[type="checkbox"]')) return;

            const row = e.target.closest('tbody tr');
            if (!row || e.target.closest('a, button')) return;

            const rows = getRows();
            const index = rows.indexOf(row);

            isMouseDown = true;
            anchorIndex = index;

            startX = e.clientX;
            startY = e.clientY;
            dragActivated = false;

            highlightRange(index, index);
            e.preventDefault();
        });

        tableElement.addEventListener('mouseenter', (e) => {
            if (!isMouseDown) return;

            if (!dragActivated) {
                const dx = Math.abs(e.clientX - startX);
                const dy = Math.abs(e.clientY - startY);

                if (dx < 3 && dy < 3) return;
                dragActivated = true;
            }

            const row = e.target.closest('tbody tr');
            if (!row) return;

            const rows = getRows();
            lastHoveredIndex = rows.indexOf(row);
            highlightRange(anchorIndex, lastHoveredIndex);
        }, true);

        tableElement.addEventListener('click', (e) => {
            if (e.target.matches('input[type="checkbox"]')) return;

            const row = e.target.closest('tbody tr');
            if (!row || e.target.closest('a, button')) return;

            if (!isMouseDown) {
                const checkbox = row.querySelector('input[type="checkbox"]');
                checkbox.checked = true;
            }
        });

        document.addEventListener('mouseup', () => {
            if (isMouseDown) {
                if (dragActivated) {
                    commitSelection();
                }
                isMouseDown = false;
                anchorIndex = null;
                lastHoveredIndex = null;
                dragActivated = false;
            }
        });
    }
    
    function initEnableTableMultiSelect() {
        const tables = document.querySelectorAll('table[data-multi-select]')
        tables.forEach(table => {
            enableTableMultiSelect(table)
        })
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function () {
            initEnableTableMultiSelect()
        })
    } else {
        initEnableTableMultiSelect()
    }
})()


//
//
//
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

            return newDirection === "asc"
                ? aText.localeCompare(bText, undefined, {numeric: true})
                : bText.localeCompare(aText, undefined, {numeric: true});
        });

        // Re-append sorted rows
        rows.forEach(row => tbody.appendChild(row));
    }

    document.querySelectorAll("table").forEach(table => {
        const firstTh = table.querySelector("th");
        if (firstTh) sortColumn(firstTh);
    });
}


function enableTableKeyboardNavigation(table) {
    let currentRow = null;

    function setActiveRow(row) {
        if (!row) return;

        if (currentRow) {
            currentRow.classList.remove('table-row-active');
        }

        currentRow = row;
        currentRow.classList.add('table-row-active');
        currentRow.scrollIntoView({block: 'nearest'});
    }

    // Click to activate row
    table.addEventListener('click', (e) => {
        const row = e.target.closest('tr');
        if (row && row.parentElement.tagName === 'TBODY') {
            setActiveRow(row);
        }
    });

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (!currentRow) return;

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            const next = currentRow.nextElementSibling;
            if (next) setActiveRow(next);
        }

        if (e.key === 'ArrowUp') {
            e.preventDefault();
            const prev = currentRow.previousElementSibling;
            if (prev) setActiveRow(prev);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    sortingTables()
})