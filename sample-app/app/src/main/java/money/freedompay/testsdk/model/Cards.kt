package money.freedompay.testsdk.model

import androidx.annotation.DrawableRes
import money.freedompay.testsdk.R

enum class Cards(@DrawableRes val cardIcon: Int) {
    VISA(R.drawable.ic_card_visa),
    MASTERCARD(R.drawable.ic_card_mastercard)
}
