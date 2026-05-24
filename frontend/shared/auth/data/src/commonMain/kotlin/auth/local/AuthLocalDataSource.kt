package auth.local

import auth.models.Profile
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class AuthLocalDataSource(
    val settings: Settings,
) {

    fun isProfileSaved(): Boolean {
        return allKeys.all {
            settings.get<String>(it) != null
        }
    }

    fun getProfile(): Profile? = if (isProfileSaved()) {
        Profile(
            name = settings[NAME_KEY, "null"],
            email = settings[EMAIL_KEY, "null"],
            id = settings[ID_KEY, "null"]
        )
    } else null

    fun setProfile(profile: Profile) {
        settings[NAME_KEY] = profile.name
        settings[EMAIL_KEY] = profile.name
        settings[ID_KEY] = profile.name
    }

    fun clearProfile() {
        allKeys.forEach {
            settings.remove(it)
        }
    }

    companion object {
        const val NAME_KEY = "user_name_key"
        const val EMAIL_KEY = "user_email_key"
        const val ID_KEY = "user_id_key"

        val allKeys = listOf(NAME_KEY, EMAIL_KEY, ID_KEY)
    }
}