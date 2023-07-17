package money.freedompay.testsdk.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import money.freedompay.qrreader.api.interfaces.AddCardViewListener
import money.freedompay.testsdk.App
import money.freedompay.testsdk.R
import money.freedompay.testsdk.databinding.AddCardFragmentBinding
import money.freedompay.testsdk.extencions.popBackStack
import money.freedompay.testsdk.wrapper.FreedomSdkWrapper

class AddCardFragment : CoreFragment(R.layout.add_card_fragment), AddCardViewListener {

    companion object {
        fun create() = AddCardFragment()
    }

    private lateinit var binding: AddCardFragmentBinding
    private lateinit var sdk: FreedomSdkWrapper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddCardFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoader(true)
        sdk = (activity?.application as App).getFreedomSdkWrapper()
        sdk.setCardView(binding.addCardView)
        binding.addCardView.setListener(this)
        lifecycleScope.launch {
            sdk.initAddingCard {
                showErrorAlert()
            }
        }
    }

    override fun onLoadFinished() {
        showLoader(false)
    }

    override fun onLoadStarted() {
        showLoader(true)
    }

    override fun onResult(isSuccess: Boolean) {
        if (isSuccess) {
            popBackStack()
        }
    }

    private fun showErrorAlert() {
        lifecycleScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_title)
                .setMessage(R.string.add_card_error)
                .setPositiveButton(R.string.close_page) { dialog, _ ->
                    dialog.dismiss()
                }.setOnDismissListener {
                    popBackStack()
                }.create().show()
        }
    }

    private fun showLoader(isVisible: Boolean) = with(binding) {
        addCardView.isVisible = !isVisible
        loader.isVisible = isVisible
    }
}
