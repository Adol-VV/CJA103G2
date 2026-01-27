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
            // 加上時間戳記防止快取
            const urlWithTimestamp = url + (url.includes('?') ? '&' : '?') + '_t=' + Date.now();

            promises.push(
                fetch(urlWithTimestamp, {
                    method: 'GET',
                    cache: 'no-store',  // 禁用快取
                    headers: {
                        'Cache-Control': 'no-cache, no-store, must-revalidate',
                        'Pragma': 'no-cache',
                        'Expires': '0'
                    }
                })
                    .then(res => {
                        if (!res.ok) throw new Error(`Load failed: ${url}`);
                        return res.text();
                    })
                    .then(html => {
                        container.innerHTML = html;
                    })
                    .catch(e => {
                        console.error('Partial load error:', e);
                        container.innerHTML = `<div class="text-danger">Error loading ${url}</div>`;
                    })
            );
        }
    });

    return Promise.all(promises);
}
