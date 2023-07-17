package money.freedompay.testsdk.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import money.freedompay.qrreader.api.ui.FreedomQRScannerView
import money.freedompay.testsdk.App
import money.freedompay.testsdk.R
import money.freedompay.testsdk.databinding.QrScannerFragmentBinding
import money.freedompay.testsdk.extencions.replaceFragment
import money.freedompay.testsdk.wrapper.FreedomSdkWrapper

class QRScannerFragment : CoreFragment(R.layout.qr_scanner_fragment), FreedomQRScannerView.ResultHandler, FreedomQRScannerView.ErrorHandler {

    companion object {
        fun create() = QRScannerFragment()
    }

    private lateinit var binding: QrScannerFragmentBinding
    private lateinit var sdk: FreedomSdkWrapper
    private var showingSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QrScannerFragmentBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sdk = (activity?.application as App).getFreedomSdkWrapper()
        setResultHandler()
        setErrorHandler()
    }

    override fun onResume() {
        super.onResume()
        binding.root.startCamera()
    }

    override fun onPause() {
        super.onPause()
        binding.root.stopCamera()
    }

    override fun handleResult(result: String) {
        replaceFragment(PaymentFragment.create(result))
    }

    override fun handleError() {
        showSnackbar(R.string.incorrect_qr) {
            setErrorHandler()
        }
    }

    private fun setResultHandler() = binding.root.setResultHandler(this)

    private fun setErrorHandler() = binding.root.setErrorHandler(this)

    private fun showSnackbar(message: Int, onDismissed: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            showingSnackbar?.dismiss()
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).addCallback(
                object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        onDismissed.invoke()
                    }
                }
            ).also {
                showingSnackbar = it
            }.show()
        }
    }
}
