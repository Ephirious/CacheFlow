package core.sw

import utils.Logg

external val self: dynamic

fun swSendMessageToClients(message: String) {
    self.clients.matchAll(js("{ type: 'window', includeUncontrolled: true }"))
        .then { clients ->
            for (client in clients) {
                client.postMessage(message)
                Logg.debug { "Sent: $message" }
            }
        }
}
