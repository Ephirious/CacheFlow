const CACHE_NAME = 'cacheflow-q'+Date.now();

console.log('[SW] Запущен с версией кэша:', CACHE_NAME);

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
    // icons
    '/ico/icon-192.png',
    '/ico/icon-512.png',
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


    if (url.pathname.startsWith('/@vite') || url.search.includes('token=')) {
        return;
    }


    if (url.origin !== location.origin) return;

    const isApiRequest = url.pathname.includes('/api/') ||
                         url.pathname.includes('/subscribe') ||
                         url.pathname.includes('/sync') ||
                         url.port === '8000';

    if (isApiRequest) return;

    event.respondWith(dynamicCacheFirst(req));
});

async function dynamicCacheFirst(request) {
    const cache = await caches.open(CACHE_NAME);

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


if (typeof localStorage === 'undefined') {
    self.localStorage = {
        getItem: function() { return null; },
        setItem: function() {},
        removeItem: function() {}
    };
}

importScripts('/db/sql-wasm.js');
importScripts('/k2ts-service.js');