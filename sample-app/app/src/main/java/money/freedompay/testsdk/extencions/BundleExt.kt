package money.freedompay.testsdk.extencions

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Fragment.withArgs(vararg params: Pair<String, Any?>): Fragment =
    this.apply { arguments = bundleOf(*params) }

fun Bundle.argString(key: String): String = this.getString(key, "")

inline fun <reified T> Bundle.fromJson(key: String): T {
    val gson = Gson()
    val jsonString = getString(key)
    return gson.fromJson(jsonString, object : TypeToken<T>() {}.type)
}

inline fun <reified T> T.toJson(): String {
    val gson = Gson()
    return gson.toJson(this, object : TypeToken<T>() {}.type)
}
