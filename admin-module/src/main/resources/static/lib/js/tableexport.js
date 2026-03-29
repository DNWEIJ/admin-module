/**
 * Table to Excel/CSV Exporter - Vanilla JavaScript
 *
 * Automatically adds export buttons to all tables (except those with data-no-export attribute)
 * Creates .xls and .csv files that can be opened in Excel
 *
 * Usage:
 * 1. Include this script in your page
 * 2. Tables will automatically get export buttons in the footer
 * 3. To exclude a table: <table data-no-export>
 * 4. To set custom filename: <table data-export-filename="myfile">
 */
(function() {
    'use strict';

    function exportTableToExcel(table, filename = 'table-export') {
        const dataType = 'application/vnd.ms-excel';

        // Clone table to avoid modifying original
        const tableClone = table.cloneNode(true);

        // Remove any export buttons from the clone
        tableClone.querySelectorAll('.table-export-btn, .table-export-btn').forEach(btn => btn.remove());

        // Remove the export footer row from the clone
        const exportFooter = tableClone.querySelector('.table-export-footer-row');
        if (exportFooter) {
            exportFooter.remove();
        }

        // Get table HTML and fix non-breaking spaces
        let tableHTML = tableClone.outerHTML.replace(/ /g, ' ');

        // Add Excel XML wrapper for better compatibility
        const excelTemplate = `
            <html xmlns:o="urn:schemas-microsoft-com:office:office" 
                  xmlns:x="urn:schemas-microsoft-com:office:excel" 
                  xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta charset="UTF-8">
                <!--[if gte mso 9]>
                <xml>
                    <x:ExcelWorkbook>
                        <x:ExcelWorksheets>
                            <x:ExcelWorksheet>
                                <x:Name>Sheet1</x:Name>
                                <x:WorksheetOptions>
                                    <x:DisplayGridlines/>
                                </x:WorksheetOptions>
                            </x:ExcelWorksheet>
                        </x:ExcelWorksheets>
                    </x:ExcelWorkbook>
                </xml>
                <![endif]-->
            </head>
            <body>
                ${tableHTML}
            </body>
            </html>
        `;

        // Create filename with .xls extension
        const fullFilename = filename.endsWith('.xls') ? filename : `${filename}.xls`;

        // Create download link
        const downloadLink = document.createElement('a');
        document.body.appendChild(downloadLink);

        // Handle different browsers
        if (navigator.msSaveOrOpenBlob) {
            // IE 10+
            const blob = new Blob(['\ufeff', excelTemplate], { type: dataType });
            navigator.msSaveOrOpenBlob(blob, fullFilename);
        } else {
            // Modern browsers
            downloadLink.href = 'data:' + dataType + ';charset=utf-8,' + encodeURIComponent(excelTemplate);
            downloadLink.download = fullFilename;
            downloadLink.click();
        }

        // Cleanup
        document.body.removeChild(downloadLink);
    }

    function exportTableToCSV(table, filename = 'table-export') {
        // Clone table to avoid modifying original
        const tableClone = table.cloneNode(true);

        // Remove any export buttons from the clone
        tableClone.querySelectorAll('.table-export-btn, .table-export-btn').forEach(btn => btn.remove());

        // Remove the export footer row from the clone
        const exportFooter = tableClone.querySelector('.table-export-footer-row');
        if (exportFooter) {
            exportFooter.remove();
        }

        const rows = [];

        // Get all rows from thead, tbody, and tfoot (excluding export footer)
        const allRows = tableClone.querySelectorAll('tr');

        allRows.forEach(row => {
            const cols = row.querySelectorAll('td, th');
            const csvRow = [];

            cols.forEach(col => {
                // Get text content and clean it up
                let text = col.textContent || col.innerText || '';
                text = text.trim();

                // Escape quotes and wrap in quotes if needed
                if (text.includes(',') || text.includes('"') || text.includes('\n')) {
                    text = '"' + text.replace(/"/g, '""') + '"';
                }

                csvRow.push(text);
            });

            rows.push(csvRow.join(','));
        });

        const csvContent = rows.join('\n');

        // Create filename with .csv extension
        const fullFilename = filename.endsWith('.csv') ? filename : `${filename}.csv`;

        // Create download link
        const downloadLink = document.createElement('a');
        document.body.appendChild(downloadLink);

        // Create blob with BOM for Excel compatibility
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });

        if (navigator.msSaveOrOpenBlob) {
            // IE 10+
            navigator.msSaveOrOpenBlob(blob, fullFilename);
        } else {
            // Modern browsers
            const url = URL.createObjectURL(blob);
            downloadLink.href = url;
            downloadLink.download = fullFilename;
            downloadLink.click();
            URL.revokeObjectURL(url);
        }

        // Cleanup
        document.body.removeChild(downloadLink);
    }

    function createExportButton(table) {
        const button = document.createElement('button');
        button.className = 'table-export-btn outline';
        button.textContent = '📥 Excel';
        button.type = 'button';

        // Add click handler
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            // Get custom filename from data attribute or generate one
            const customFilename = table.getAttribute('data-export-filename');
            const now = new Date();
            const dateSuffix = now.toISOString().slice(0, 10); // YYYY-MM-DD
            const filename = customFilename
                ? `${customFilename}-${dateSuffix}`
                : `table-export-${Date.now()}`;

            exportTableToExcel(table, filename);
        });
        return button;
    }

    function createCSVButton(table) {
        const button = document.createElement('button');
        button.className = 'table-export-btn outline';
        button.textContent = '📥 CSV';
        button.type = 'button';

        // Add click handler
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            // Get custom filename from data attribute or generate one
            const customFilename = table.getAttribute('data-export-filename');
            const now = new Date();
            const dateSuffix = now.toISOString().slice(0, 10); // YYYY-MM-DD
            const filename = customFilename
                ? `${customFilename}-${dateSuffix}`
                : `table-export-${Date.now()}`;

            exportTableToCSV(table, filename);
        });
        return button;
    }

    function getTableColumnCount(table) {
        // Try to get column count from first row of thead, tbody, or table
        const firstRow = table.querySelector('thead tr, tbody tr, tr');
        if (!firstRow) return 1;

        let colCount = 0;
        const cells = firstRow.querySelectorAll('th, td');
        cells.forEach(cell => {
            const colspan = parseInt(cell.getAttribute('colspan')) || 1;
            colCount += colspan;
        });

        return colCount || 1;
    }

    function countTableRows(table) {
        // Count rows in tbody if it exists, otherwise count all data rows
        const tbody = table.querySelector('tbody');
        if (tbody) {
            return tbody.querySelectorAll('tr').length;
        }

        // No tbody: count all rows except thead and tfoot rows
        const allRows = table.querySelectorAll('tr');
        const theadRows = table.querySelectorAll('thead tr').length;
        const tfootRows = table.querySelectorAll('tfoot tr').length;

        return allRows.length - theadRows - tfootRows;
    }


    function addExportButtonToTable(table) {

        if (table.hasAttribute('data-no-export')) return;
        if (table.querySelector('.table-export-btn')) return;

        const excelButton = createExportButton(table);
        const csvButton = createCSVButton(table);
        const rowCount = countTableRows(table);
        const colCount = getTableColumnCount(table);

        let tfoot = table.querySelector('tfoot');

        // CASE 1: NO FOOTER → original behavior (colspan layout)
        if (!tfoot || !tfoot.querySelector('tr')) {

            if (!tfoot) {
                tfoot = document.createElement('tfoot');
                table.appendChild(tfoot);
            }

            const footerRow = document.createElement('tr');
            const footerCell = document.createElement('td');
            footerCell.setAttribute('colspan', colCount);
            footerCell.style.textAlign = 'left';
            footerCell.style.padding = '1px';

            const container = document.createElement('div');
            container.style.display = 'flex';
            container.style.justifyContent = 'space-between';
            container.style.alignItems = 'center';

            const countText = document.createElement('span');
            countText.textContent = `Total amount of records: ${rowCount}`;

            const buttonContainer = document.createElement('div');
            buttonContainer.style.display = 'flex';
            buttonContainer.style.gap = '4px';
            buttonContainer.appendChild(csvButton);
            buttonContainer.appendChild(excelButton);

            container.appendChild(countText);
            container.appendChild(buttonContainer);
            footerCell.appendChild(container);
            footerRow.appendChild(footerCell);
            tfoot.appendChild(footerRow);

            return;
        }

        // CASE 2: FOOTER EXISTS → use first + last cell
        const footerRow = tfoot.querySelector('tr:last-child');

        const firstCell = footerRow.firstElementChild;
        if (firstCell) {
            firstCell.textContent = `Total amount of records: ${rowCount}`;
        }

        const lastCell = footerRow.lastElementChild;
        if (lastCell) {
            let buttonContainer = lastCell.querySelector('div');
            if (!buttonContainer) {
                buttonContainer = document.createElement('div');
                lastCell.appendChild(buttonContainer);
            }
            buttonContainer.style.display = 'flex';
            buttonContainer.style.gap = '4px';
            buttonContainer.style.flexWrap = 'nowrap';
            buttonContainer.innerHTML = '';
            buttonContainer.appendChild(csvButton);
            buttonContainer.appendChild(excelButton);
            lastCell.style.textAlign = 'right';
        }
    }



    function initializeExportButtons() {
        // Find all tables
        const tables = document.querySelectorAll('table:not([data-no-export])');

        tables.forEach(table => {
            addExportButtonToTable(table);
        });
    }

    function addButtonStyles() {
        if (document.getElementById('table-export-styles')) {
            return; // Styles already added
        }

        const style = document.createElement('style');
        style.id = 'table-export-styles';
        style.textContent = `
            .table-export-count {
                font-weight: bold;
            }
            
            .table-export-footer-row {
                background-color: var(--pico-table-border-color);
            }
        `;
        document.head.appendChild(style);
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            addButtonStyles();
            initializeExportButtons();
        });
    } else {
        addButtonStyles();
        initializeExportButtons();
    }

    // Expose functions globally for manual use
    window.exportTableToExcel = exportTableToExcel;
    window.exportTableToCSV = exportTableToCSV;

})();