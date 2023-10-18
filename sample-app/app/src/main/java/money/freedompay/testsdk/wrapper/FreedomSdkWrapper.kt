package money.freedompay.testsdk.wrapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import money.freedompay.qrreader.api.FreedomQRClient
import money.freedompay.qrreader.api.models.Url
import money.freedompay.qrreader.api.models.responses.PaymentStatusResponse
import money.freedompay.qrreader.api.models.responses.TokenizedCardDetailResponse
import money.freedompay.qrreader.api.ui.AddCardView
import money.freedompay.testsdk.model.RequestResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val USER_ID = "your_user_id"
private const val MERCHANT_ID = "your_merchant_id"
private const val SECRET_KEY = "your_secret_key"

class FreedomSdkWrapper {

    private val freedomPaySdk = FreedomQRClient.initialize(SECRET_KEY, MERCHANT_ID)

    fun setCardView(view: AddCardView) {
        freedomPaySdk.setCardView(view)
    }

    suspend fun paymentByCard(
        customerId: String,
        cardToken: String
    ): RequestResult<PaymentStatusResponse> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                freedomPaySdk.payByCard(
                    customerId, USER_ID, cardToken,
                    {
                        continuation.resume(RequestResult.Success(it))
                    },
                    {
                        continuation.resume(RequestResult.Error(it))
                    },
                    {
                        continuation.resume(RequestResult.FatalError(it))
                    }
                )
            }
        }
    }

    suspend fun getPaymentStatus(customerId: String): RequestResult<PaymentStatusResponse> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                freedomPaySdk.getPaymentStatus(
                    customerId,
                    {
                        continuation.resume(RequestResult.Success(it))
                    },
                    {
                        continuation.resume(RequestResult.Error(it))
                    },
                    {
                        continuation.resume(RequestResult.FatalError(it))
                    }
                )
            }
        }
    }

    suspend fun getPaymentStatus(url: Url): RequestResult<PaymentStatusResponse> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                freedomPaySdk.getPaymentStatus(
                    url.customerId,
                    {
                        continuation.resume(RequestResult.Success(it))
                    },
                    {
                        continuation.resume(RequestResult.Error(it))
                    },
                    {
                        continuation.resume(RequestResult.FatalError(it))
                    }
                )
            }
        }
    }

    suspend fun getListCards(): RequestResult<List<TokenizedCardDetailResponse>> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                freedomPaySdk.getTokenizedCardList(
                    USER_ID,
                    {
                        continuation.resume(RequestResult.Success(it.card))
                    },
                    {
                        continuation.resume(RequestResult.Error(it))
                    },
                    {
                        continuation.resume(RequestResult.FatalError(it))
                    }
                )
            }
        }
    }

    suspend fun removeCard(cardToke: String): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                freedomPaySdk.removeTokenizedCard(
                    USER_ID, cardToke,
                    {
                        continuation.resume(true)
                    },
                    {
                        continuation.resume(false)
                    },
                    {
                        continuation.resume(false)
                    }
                )
            }
        }
    }

    suspend fun initAddingCard(onError: () -> Unit) {
        return withContext(Dispatchers.Default) {
            freedomPaySdk.initAddingCard(
                USER_ID,
                {
                    onError.invoke()
                },
                {
                    onError.invoke()
                }
            )
        }
    }
}
