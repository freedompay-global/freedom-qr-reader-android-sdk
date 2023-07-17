package money.freedompay.testsdk.utils

import money.freedompay.testsdk.model.Cards

object CardValidator {
    private enum class EnumCardRegex(val regex: Regex, val type: Cards) {
        VISA(Regex("^(4[0-9][0-9])"), Cards.VISA),
        MASTERCARD(Regex("^(5[0-5]|2[2-6][0-9]|27[0-2])"), Cards.MASTERCARD)
    }

    fun getCardType(mask: String): Cards {
        val digits = mask.take(3)
        val item = EnumCardRegex.values().firstOrNull { it.regex.matches(digits) }
        return item?.type ?: Cards.VISA
    }
}
