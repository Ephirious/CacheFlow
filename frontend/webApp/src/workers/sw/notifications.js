export async function showPushNotification(event) {
    let data;
    try {
        data = event.data ? event.data.text() : "Новое уведомление";
    } catch (err) {
        console.error('[SW] Push data error:', err);
        data = "Новое уведомление";
    }

    const options = {
        body: data,
        icon: '/icon.png',
        badge: '/badge.png',
        tag: 'weekly-reminder-' + Date.now(),
        vibrate: [100, 50, 100]
    };

    console.debug(`[SW] Try to push: ${data}`);
    return self.registration.showNotification("CacheFlow", options);
}

export function handleNotificationClick(event) {
    event.notification.close();
    event.waitUntil(
        self.clients.openWindow("/")
    );
}