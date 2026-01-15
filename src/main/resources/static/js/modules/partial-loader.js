/**
 * Partial Loader Module
 * Loads HTML partials into containers with data-partial attribute.
 */

export async function loadPartials() {
    const containers = document.querySelectorAll('[data-partial]');
    const promises = [];

    containers.forEach(container => {
        const url = container.getAttribute('data-partial');
        if (url) {
            promises.push(
                fetch(url)
                    .then(res => {
                        if (!res.ok) throw new Error(`Load failed: ${url}`);
                        return res.text();
                    })
                    .then(html => {
                        container.innerHTML = html;
                    })
                    .catch(e => {
                        console.error(e);
                        container.innerHTML = `<div class="text-danger">Error loading ${url}</div>`;
                    })
            );
        }
    });

    return Promise.all(promises);
}
