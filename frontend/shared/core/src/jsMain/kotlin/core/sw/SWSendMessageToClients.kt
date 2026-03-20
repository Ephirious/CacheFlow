package core.sw

external val self: dynamic

fun swSendMessageToClients(message: String) {
    self.clients.matchAll(js("{ type: 'window', includeUncontrolled: true }"))
        .then { clients ->
            for (client in clients) {
                client.postMessage(message)
                println("[INFO-ServiceWorker] Sent: $message")
            }
        }
}
