function dropdownWithDataList(textInput, idInput, listEl) {
    const options = Array.from(listEl.options)
    const map = new Map(options.map(o => [o.value, o.dataset.id]))

    /* update ID when a valid option is chosen */
    textInput.addEventListener('input', () => {
        const q = textInput.value.trim().toLowerCase()
        idInput.value = map.get(textInput.value) ?? ''
        listEl.innerHTML = ''
        if (q.length < 2) return
        options.forEach(opt => {
            if (opt.value.toLowerCase().startsWith(q)) {
                listEl.appendChild(opt.cloneNode(true))
            }
        })
        // see if only one option matches, if so, set the value and ID to that option
        const matches = options.filter(opt =>
            opt.value.toLowerCase().startsWith(q)
        )
        if (matches.length === 1) {
            textInput.value = matches[0].value
            idInput.value = matches[0].dataset.id
        }
    })
    textInput.addEventListener('focus', () => {
        listEl.innerHTML = ''
    })

    /* block submit if text not valid */
    textInput.form.addEventListener('submit', e => {
        if (!map.has(textInput.value)) {
            e.preventDefault()
            textInput.setCustomValidity('Please select a valid option')
            textInput.reportValidity()
        } else {
            textInput.setCustomValidity('')
        }
    })
}