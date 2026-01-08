document.addEventListener('htmx:configRequest', e => {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (token && header) {
        e.detail.headers[header] = token;
    }
});