package money.freedompay.testsdk.utils

import money.freedompay.testsdk.model.EnumLink

internal object QRValidator {
    private const val EXPECTED_DOMAIN_NAME = "customer.freedompay.money"

    fun getUrlType(url: String): EnumLink {
        return if (url.contains(EXPECTED_DOMAIN_NAME)) {
            EnumLink.DEEP_LINK
        } else EnumLink.QR
    }
}
