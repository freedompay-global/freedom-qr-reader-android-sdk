package money.freedompay.testsdk.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import money.freedompay.testsdk.R

enum class InvoiceStatus(
    private val status: String,
    @StringRes
    val text: Int,
    @DrawableRes
    val icon: Int
) {
    NEW("new", R.string.invoice_status_new, R.drawable.ic_ok),
    PROCESS("process", R.string.invoice_status_process, R.drawable.ic_process),
    OK("ok", R.string.invoice_status_ok, R.drawable.ic_ok),
    FAILED("failed", R.string.invoice_status_failed, R.drawable.ic_error),
    INCOMPLETE("incomplete", R.string.invoice_status_incomplete, R.drawable.ic_incomplete),
    UNKNOWN("unknown", R.string.invoice_status_unknown, R.drawable.ic_incomplete);

    companion object {
        fun getTextBy(status: String): InvoiceStatus {
            return values().firstOrNull { it.status == status } ?: UNKNOWN
        }
    }
}
