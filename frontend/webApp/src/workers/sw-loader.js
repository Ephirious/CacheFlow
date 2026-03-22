const BUILD_HASH = typeof __BUILD_HASH__ !== 'undefined' ? __BUILD_HASH__ : null;
const CACHE_NAME = BUILD_HASH ? `cacheflow-h${BUILD_HASH}` : null;

console.log(CACHE_NAME
    ? `[SW] Запущен с версией кэша: ${CACHE_NAME}`
    : '[SW] Кэширование (offline-first mode) отключено (BUILD_HASH не найден)'
);

const ASSETS_TO_CACHE = [
    '/',
    '/index.html',
    '/manifest.json',

    '/ico/icon-192.png',
    '/ico/icon-512.png',

    '/src/workers/sw-loader.js',
    '/src/workers/sqljs.worker.js',
];


self.addEventListener('install', (event) => {
    event.waitUntil(
        Promise.all([
            self.skipWaiting(),
            CACHE_NAME ? caches.open(CACHE_NAME).then(async (cache) => {
                for (const url of ASSETS_TO_CACHE) {
                    try {
                        await cache.add(url);
                    } catch (e) {
                        console.warn('[SW] Failed to cache:', url);
                    }
                }
            }) : Promise.resolve()
        ])
    );
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        Promise.all([
            self.clients.claim(),
            CACHE_NAME ? caches.keys().then((cacheNames) => {
                return Promise.all(
                    cacheNames.filter((name) => name !== CACHE_NAME)
                        .map((name) => caches.delete(name))
                );
            }) : Promise.resolve()
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

    if (!CACHE_NAME) {
        event.respondWith(fetch(req));
        return;
    }

    if (req.mode === 'navigate') {
        event.respondWith(networkFirst(req));
        return;
    }

    event.respondWith(dynamicCacheFirst(req));
});

async function networkFirst(request) {
    const cache = await caches.open(CACHE_NAME);
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
        getItem: function () {
            return null;
        },
        setItem: function () {
        },
        removeItem: function () {
        }
    };
}

import initSqlJs from 'sql.js/dist/sql-wasm.js';
import sqlWasmUrl from 'sql.js/dist/sql-wasm.wasm?url';
import * as KService from 'k2ts-service';

self.initSqlJs = initSqlJs;
self.sqlWasmUrl = sqlWasmUrl;

KService.main();