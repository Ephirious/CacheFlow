
const HOSTS_TO_CACHE = [
    location.origin,
    'cdn.tailwindcss.com',
    'fonts.googleapis.com',
    'fonts.gstatic.com'
];

import { networkFirst, dynamicCacheFirst } from './sw/strategies.js';
import { showPushNotification, handleNotificationClick } from './sw/notifications.js';

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
    '/src/workers/sw.js',
    '/src/workers/sw/notifications.js',
    '/src/workers/sw/strategies.js',
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

    if (req.destination === "style") {
        event.respondWith(dynamicCacheFirst(req));
        return;
    }

    const url = new URL(req.url);
    if (url.pathname.startsWith('/@vite') || url.search.includes('token=')) return;

    const isAllowedHost = HOSTS_TO_CACHE.includes(url.host) || url.origin === location.origin;

    if (!isAllowedHost) return;

    const isApiRequest = url.pathname.includes('/api/') ||
        url.pathname.includes('/subscribe') ||
        url.pathname.includes('/sync') ||
        url.port === '8000';

    if (isApiRequest || !CACHE_NAME) {
        return;
    }

    if (req.mode === 'navigate') {
        event.respondWith(networkFirst(req, CACHE_NAME));
    } else {
        event.respondWith(dynamicCacheFirst(req, CACHE_NAME));
    }
});

self.addEventListener('push', (event) => {
    event.waitUntil(showPushNotification(event));
});

self.addEventListener('notificationclick', (event) => {
    handleNotificationClick(event);
});