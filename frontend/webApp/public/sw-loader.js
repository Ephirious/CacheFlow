const CACHE_NAME = 'cacheflow-v1';


const ASSETS_TO_CACHE = [
    '/',
    '/index.html',
    '/sw-loader.js',
    '/k2ts-service.js',
    '/manifest.json',
    // db files
    '/db/sql-wasm.js',
    '/db/sql-wasm.wasm',
    '/db/sqljs.worker.js',
    '/db/worker.sql-wasm.js',
    // icons: TODO
];


self.addEventListener('install', (event) => {
    self.skipWaiting();
    event.waitUntil(
        caches.open(CACHE_NAME).then(async (cache) => {
            for (const url of ASSETS_TO_CACHE) {
                try {
                    await cache.add(url);
                } catch (e) {
                    console.warn('[SW] Failed to cache:', url);
                }
            }
        })
    );
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        Promise.all([
            self.clients.claim(),
            caches.keys().then((cacheNames) => {
                return Promise.all(
                    cacheNames.filter((name) => name !== CACHE_NAME)
                              .map((name) => caches.delete(name))
                    );
            })
        ])
    );
});

self.addEventListener('fetch', (event) => {
    const req = event.request;

    if (req.method !== 'GET') return;

    const url = new URL(req.url);
    if (url.origin !== location.origin) return;

    const isApiRequest = url.pathname.includes('/api/') ||
                         url.pathname.includes('/subscribe') ||
                         url.pathname.includes('/sync') ||
                         url.port === '8000';

    if (isApiRequest) return;

    event.respondWith(dynamicCacheFirst(req));
});

async function dynamicCacheFirst(request) {
    const cached = await caches.match(request);

    if (cached) {
        return cached;
    }

    try {
        const response = await fetch(request);

        const contentType = response.headers.get('content-type');
        const isStaticAsset = contentType && (
            contentType.includes('javascript') ||
            contentType.includes('wasm') ||
            contentType.includes('html') ||
            contentType.includes('css') ||
            contentType.includes('image')
        );

        if (response.status === 200 && isStaticAsset) {
            const cache = await caches.open(CACHE_NAME);
            cache.put(request, response.clone());
        }
        return response;
    } catch (e) {
        if (request.mode === 'navigate') {
            const offlineShell = await caches.match('/index.html');
            if (offlineShell) return offlineShell;
        }
        // hi from lighthouse!!
        return new Response('Network error occurred', {
            status: 408,
            statusText: 'Network error occurred'
        });
    }
}


if (typeof localStorage === 'undefined') {
    self.localStorage = {
        getItem: function() { return null; },
        setItem: function() {},
        removeItem: function() {}
    };
}

importScripts('db/worker.sql-wasm.js');
importScripts('./k2ts-service.js');