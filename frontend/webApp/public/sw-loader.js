self.addEventListener('install', (event) => {
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(self.clients.claim());
});

if (typeof localStorage === 'undefined') {
    self.localStorage = {
        getItem: function() { return null; },
        setItem: function() {},
        removeItem: function() {}
    };
}

importScripts('db/worker.sql-wasm.js');
importScripts('./k2ts-service.js');