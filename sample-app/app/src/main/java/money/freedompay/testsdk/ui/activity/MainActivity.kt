package money.freedompay.testsdk.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import money.freedompay.testsdk.R
import money.freedompay.testsdk.extencions.replaceFragment
import money.freedompay.testsdk.ui.fragments.PaymentFragment
import money.freedompay.testsdk.ui.fragments.RootFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        replaceFragment(RootFragment.create())
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val url = it.data.toString()
            replaceFragment(PaymentFragment.create(url))
        }
    }
}
