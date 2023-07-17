package money.freedompay.testsdk.extencions

import money.freedompay.testsdk.model.Cards
import money.freedompay.testsdk.utils.CardValidator

fun String.maskedCardPan(): String {
    val cardPan = this.length
    return "•••• ".plus(this.substring(cardPan - 4, cardPan))
}

fun String.getCardType(): Cards {
    return CardValidator.getCardType(this)
}
