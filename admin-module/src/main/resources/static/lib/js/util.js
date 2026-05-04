function copyToClipboard(el) {
    const ref = el.previousElementSibling;
    if (!ref) return;
    // input or text element
    const originalText = ref.value !== undefined ? ref.value : ref.innerText;
    navigator.clipboard.writeText(originalText);
    if (ref.value !== undefined) {
        ref.value = 'Copied!';
        setTimeout(() => ref.value = originalText, 800);
    } else {
        ref.innerText = 'Copied!';
        setTimeout(() => ref.innerText = originalText, 800);
    }
}
function setupCopyClipBoard() {
    document.querySelectorAll('[data-copy-element]').forEach(el => {
        el.insertAdjacentHTML('afterend',
            `<a  title="Copy ${el.dataset.copyElement}">📋</a>`
        );
        const button = el.nextElementSibling;
        button.onclick = () => copyToClipboard(button);
    });
}

function toggleAllCheckBoxesInSurroundingElement(masterCheckbox, surroundingElement) {

    const fieldset = masterCheckbox.closest(surroundingElement)
    if (!fieldset) return

    const checkboxes = fieldset.querySelectorAll('input[type="checkbox"]')

    checkboxes.forEach(function (checkbox) {
        if (checkbox !== masterCheckbox) {
            checkbox.checked = masterCheckbox.checked
        }
    })
}

function highlightRowOnTable() {
    /** every table has a highlight on it **/
    document.querySelectorAll('table:not([data-no-highlight]) tbody tr').forEach(row => {
        row.addEventListener('mouseover', () => row.classList.add('highlight'))
        row.addEventListener('mouseout', () => row.classList.remove('highlight'))
    })
}

function setupEventSource() {
    const eventSource = new EventSource('/log/stream')

    eventSource.onmessage = function (event) {
        console.log('[SERVER LOG]', event.data)
    }

    eventSource.onerror = function (err) {
        console.error('Log stream error:', err)
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // setupEventSource()
    setupCopyClipBoard()

    document.body.addEventListener("refreshPage", function (evt) {
        const detail = evt.detail || ''
        const url = detail.url
        if (url) {
            window.location.href = url
        } else {
            window.location.reload()

        }
    })

    document.body.addEventListener('htmx:configRequest', e => {
        const token = document.querySelector('meta[name="_csrf"]')?.content
        const header = document.querySelector('meta[name="_csrf_header"]')?.content
        if (token && header) {
            e.detail.headers[header] = token
        }
    })
    document.body.addEventListener("htmx:responseError", function (evt) {
        const status = evt.detail.xhr.status
        const xhr = evt.detail.xhr

        if (status === 401 || status === 403) {
            window.location.href = "/admin/login"
        }
        if (status == 500) {

            var message = "Something went really wrong. refresh the page, and try again. If it still doesn't work, notify your manager"
            const contentType = xhr.getResponseHeader("Content-Type") || "";
            if (contentType.includes("application/json")) {
                try {
                    const json = JSON.parse(xhr.responseText);
                    message = json.message || json.error || message;
                } catch (_) {}
            } else if (xhr.responseText) {
                message = xhr.responseText.substring(0, 300);
            }
            document.getElementById('error-message').textContent = message
            document.getElementById('error-modal').showModal()
        }
    })

    highlightRowOnTable()

    document.body.addEventListener("htmx:afterSwap", function (event) {
        if (event.target.id === "notification-message-modal") {
            setTimeout(() => {
                const el = document.getElementById("notification-message-modal");
                if (el) {
                    el.classList.remove("show");
                }
            }, 4000);
        }
    });


    // todo this makes the tool tip blink but it doesnt go away.
    // document.querySelectorAll('[data-tooltip]').forEach(el => {
    //     el.addEventListener('mouseenter', () => {
    //         const tooltip = el.dataset.tooltip
    //         setTimeout(() => {
    //             el.dataset.tooltip = ''
    //             setTimeout(() => el.dataset.tooltip = tooltip, 100)
    //         }, 1000)
    //     })
    // })
})

