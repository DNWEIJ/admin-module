// table-chart.js
export function createTableChart(config) {
    const {
        tableSelector,
        extractRow,     // (tds, tr) => object
        groupKey,       // (row, mode) => string
        valueKey,       // (row, field) => number
        datasetKey,     // (row) => string (series split, optional)
        title = "Chart",
        chartCanvasId = "chart",
        dialogId = "chartDialog"
    } = config

    let chart

    function ensureDialog() {
        let dialog = document.getElementById(dialogId)

        if (!dialog) {
            dialog = document.createElement("dialog")
            dialog.id = dialogId

            dialog.innerHTML = `
                <article>
                    <header style="display:flex; justify-content:space-between; align-items:center">
                        <strong>${title}</strong>
                        <button type="button" id="${dialogId}_close">✕</button>
                    </header>
                    <canvas id="${chartCanvasId}"></canvas>
                </article>
            `

            document.body.appendChild(dialog)

            dialog.querySelector(`#${dialogId}_close`)
                .addEventListener("click", () => dialog.close())
        }

        return dialog
    }

    function parseTable() {
        const rows = document.querySelectorAll(`${tableSelector} tbody tr`)
        const data = []

        rows.forEach(tr => {
            const tds = tr.querySelectorAll("td")
            const row = extractRow(tds, tr)
            if (row) data.push(row)
        })

        return data
    }

    function group(data, mode, field) {
        const map = {}

        data.forEach(row => {
            const gKey = groupKey(row, mode)
            const dKey = datasetKey ? datasetKey(row) : "default"
            const val = valueKey(row, field)

            if (!map[gKey]) map[gKey] = {}
            if (!map[gKey][dKey]) map[gKey][dKey] = 0

            map[gKey][dKey] += val
        })

        return map
    }

    function buildChartData(grouped) {
        const labels = Object.keys(grouped).sort()
        const series = new Set()

        labels.forEach(l => {
            Object.keys(grouped[l]).forEach(s => series.add(s))
        })

        const datasets = Array.from(series).map(s => ({
            label: s,
            data: labels.map(l => grouped[l][s] || 0)
        }))

        return { labels, datasets }
    }

    function render(field, mode) {
        const dialog = ensureDialog()
        const canvas = dialog.querySelector(`#${chartCanvasId}`)

        const raw = parseTable()
        const grouped = group(raw, mode, field)
        const data = buildChartData(grouped)


        if (chart) chart.destroy()

        chart = new Chart(canvas.getContext("2d"), {
            type: "line",
            data,
            options: {
                responsive: true,
                interaction: { mode: "nearest", intersect: false }
            }
        })

        dialog.showModal()
    }

    return { render }
}