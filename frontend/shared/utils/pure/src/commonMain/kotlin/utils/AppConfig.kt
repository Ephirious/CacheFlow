package utils

@Suppress("ConstPropertyName")
object AppConfig {
    const val isDebuggable = false
    const val pushVapidPublicKey = "BDJfPY2ZNH_L1IIWABoh5_ELqPjSM4osBATVW0bzaaWxdEMehnPgtRNtXq86K34Z3w5EvOt7obu142LnrhhIE_A"

    const val serverIP = "localhost"
    const val serverPort = 8000
    const val urlSchemeString = "http://" // used for push registration
}