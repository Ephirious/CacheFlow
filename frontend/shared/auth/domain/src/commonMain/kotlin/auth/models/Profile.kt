package auth.models

import kotlin.js.JsExport


@JsExport
data class Profile(
    val name: String,
    val email: String,
    val id: String
)