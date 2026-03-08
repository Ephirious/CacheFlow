package interopSample.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class InteropSampleLocalDataSource(
    val settings: Settings
) {

    fun setSampleText(text: String) {
        settings[SAMPLE_TEXT_KEY] = text
    }

    fun getSampleText(): String =
        settings[SAMPLE_TEXT_KEY, ""]


    companion object {
        const val SAMPLE_TEXT_KEY = "sampleTextKey"
    }
}