package money.freedompay.testsdk

import android.app.Application
import money.freedompay.testsdk.wrapper.FreedomSdkWrapper

class App : Application() {
    private val freedomSdkWrapper = FreedomSdkWrapper()
    fun getFreedomSdkWrapper() = freedomSdkWrapper
}
