export async function networkFirst(request, cacheName) {
    const cache = await caches.open(cacheName);
    try {
        const response = await fetch(request);
        if (response.status === 200) {
            cache.put(request, response.clone());
        }
        return response;
    } catch (e) {
        return await cache.match(request) || await cache.match('/index.html');
    }
}

export async function dynamicCacheFirst(request, cacheName) {
    const cache = await caches.open(cacheName);
    const cached = await cache.match(request);
    if (cached) return cached;

    try {
        const response = await fetch(request);
        if (response && response.status === 200 && response.type === 'basic') {
            cache.put(request, response.clone());
        }
        return response;
    } catch (e) {
        if (request.mode === 'navigate') {
            const offlineShell = await cache.match('/index.html');
            if (offlineShell) return offlineShell;
        }
        throw e;
    }
}