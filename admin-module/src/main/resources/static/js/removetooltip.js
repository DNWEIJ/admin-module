document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('[data-tooltip]').forEach(el => {
        el.addEventListener('mouseenter', () => {
            const tooltip = el.dataset.tooltip;
            setTimeout(() => {
                el.dataset.tooltip = '';
                setTimeout(() => el.dataset.tooltip = tooltip, 100);
            }, 1000);
        });
    });
});