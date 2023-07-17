package money.freedompay.testsdk.model

import money.freedompay.qrreader.api.models.responses.ErrorResponse

sealed class RequestResult<T> {
    class Success<T>(val data: T) : RequestResult<T>()
    class Error<T>(val error: ErrorResponse) : RequestResult<T>()
    class FatalError<T>(val exception: Exception) : RequestResult<T>()
}
