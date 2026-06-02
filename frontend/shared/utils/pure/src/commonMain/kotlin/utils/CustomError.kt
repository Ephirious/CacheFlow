package utils

import localization.localz
import kotlin.js.JsExport

@JsExport
interface CustomError


class InvalidDomainException(val error: CustomError) :
    RuntimeException(localz.byValidation(error))