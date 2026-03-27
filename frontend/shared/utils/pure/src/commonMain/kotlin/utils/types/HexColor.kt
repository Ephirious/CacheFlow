package utils.types

import kotlin.js.JsExport

@JsExport
data class HexColor(private val hex: String) {
    val normalizedHex: String = if (hex.startsWith("#")) hex else "#$hex"

    override fun toString() = normalizedHex
}

