document.addEventListener('htmx:afterSettle', function (evt) {
    const downloadLink = evt.target.querySelector('[data-download-url]');
    if (downloadLink) {
        const url = downloadLink.getAttribute('data-download-url');
        window.location.href = url;
    }
});
