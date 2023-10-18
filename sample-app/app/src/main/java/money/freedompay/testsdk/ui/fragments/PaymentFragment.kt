package money.freedompay.testsdk.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import money.freedompay.qrreader.api.models.Url
import money.freedompay.qrreader.api.models.responses.PaymentStatusResponse
import money.freedompay.qrreader.api.models.responses.TokenizedCardDetailResponse
import money.freedompay.testsdk.App
import money.freedompay.testsdk.R
import money.freedompay.testsdk.databinding.PaymentFragmentBinding
import money.freedompay.testsdk.extencions.*
import money.freedompay.testsdk.model.EnumLink
import money.freedompay.testsdk.model.InvoiceStatus
import money.freedompay.testsdk.model.RequestResult
import money.freedompay.testsdk.ui.adapters.ChooseCardAdapter
import money.freedompay.testsdk.utils.QRValidator
import money.freedompay.testsdk.wrapper.FreedomSdkWrapper

private const val EXTRA_CUSTOMER_ID = "EXTRA_CUSTOMER_ID"

class PaymentFragment :
    CoreFragment(R.layout.payment_fragment),
    AdapterView.OnItemSelectedListener {

    companion object {
        fun create(
            customerId: String
        ) = PaymentFragment().withArgs(
            EXTRA_CUSTOMER_ID to customerId
        )
    }

    private lateinit var customerId: String

    private lateinit var binding: PaymentFragmentBinding
    private lateinit var sdk: FreedomSdkWrapper
    private lateinit var adapter: ChooseCardAdapter

    private var chooseCardToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            customerId = argString(EXTRA_CUSTOMER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sdk = (activity?.application as App).getFreedomSdkWrapper()
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        chooseCardToken = adapter.getItem(position).cardToken
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // not need
    }

    private fun loadData() {
        showShimmer()
        when (QRValidator.getUrlType(customerId)) {
            EnumLink.QR -> getPaymentStatus(customerId)
            EnumLink.DEEP_LINK -> {
                val url = Url.initialize(customerId)
                if (url != null) {
                    customerId = url.customerId
                    getPaymentStatusByUrl(url)
                }
            }
        }
    }

    private fun getPaymentStatus(customer: String) {
        lifecycleScope.launch {
            when (val statusData = sdk.getPaymentStatus(customer)) {
                is RequestResult.Success -> {
                    when (InvoiceStatus.getTextBy(statusData.data.invoiceStatus)) {
                        InvoiceStatus.NEW -> processNewInvoice(statusData.data)
                        InvoiceStatus.UNKNOWN -> showErrorAlert(R.string.payment_status_error)
                        else -> processOtherInvoice(statusData.data)
                    }
                }
                is RequestResult.Error -> {
                    showErrorAlert(R.string.payment_status_error)
                }
                is RequestResult.FatalError -> {
                    showErrorAlert(R.string.payment_status_error)
                }
            }
        }
    }

    private fun getPaymentStatusByUrl(url: Url) {
        lifecycleScope.launch {
            when (val statusData = sdk.getPaymentStatus(url)) {
                is RequestResult.Success -> {
                    when (InvoiceStatus.getTextBy(statusData.data.invoiceStatus)) {
                        InvoiceStatus.NEW -> processNewInvoice(statusData.data)
                        InvoiceStatus.UNKNOWN -> showErrorAlert(R.string.payment_status_error)
                        else -> processOtherInvoice(statusData.data)
                    }
                }
                is RequestResult.Error -> {
                    showErrorAlert(R.string.payment_status_error)
                }
                is RequestResult.FatalError -> {
                    showErrorAlert(R.string.payment_status_error)
                }
            }
        }
    }

    private fun processNewInvoice(paymentStatus: PaymentStatusResponse) {
        lifecycleScope.launch {
            when (val cards = sdk.getListCards()) {
                is RequestResult.Success -> {
                    if (cards.data.isEmpty()) {
                        showErrorAlert(R.string.cards_not_found)
                    } else {
                        showPaymentData(paymentStatus, cards.data)
                    }
                }
                is RequestResult.Error -> {
                    showErrorAlert(R.string.load_cards_error)
                }
                is RequestResult.FatalError -> {
                    showErrorAlert(R.string.load_cards_error)
                }
            }
        }
    }

    private fun processOtherInvoice(status: PaymentStatusResponse) {
        showStatusData(status)
    }

    private fun showErrorAlert(message: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(R.string.it_is_clear) { dialog, _ ->
                    dialog.dismiss()
                }.setOnDismissListener {
                    backToRoot()
                }.create().show()
        }
    }

    private fun showPaymentData(
        data: PaymentStatusResponse,
        cards: List<TokenizedCardDetailResponse>
    ) = with(binding) {
        showPaymentContainer()

        val status = InvoiceStatus.getTextBy(data.invoiceStatus)
        statusImageView.setImageResource(status.icon)
        statusTextView.setText(status.text)
        amountTextView.text = data.amount
        descriptionTextView.text = data.description
        spinner.onItemSelectedListener = this@PaymentFragment
        adapter = ChooseCardAdapter(requireContext(), cards)
        spinner.adapter = adapter

        button.setText(R.string.payment)
        button.setOnClickListener {
            chooseCardToken?.let { cardToken ->
                showShimmer()
                lifecycleScope.launch {
                    when (val result = sdk.paymentByCard(customerId, cardToken)) {
                        is RequestResult.Success -> {
                            showStatusData(result.data)
                        }
                        is RequestResult.Error -> {
                            showErrorAlert(R.string.payment_error)
                        }
                        is RequestResult.FatalError -> {
                            showErrorAlert(R.string.payment_error)
                        }
                    }
                }
            }
        }
    }

    private fun showStatusData(data: PaymentStatusResponse) = with(binding) {
        changeBackPressedCallback {
            backToRoot()
        }
        showStatusContainer()

        val status = InvoiceStatus.getTextBy(data.invoiceStatus)
        statusImageView.setImageResource(status.icon)
        statusTextView.setText(status.text)
        statusAmountTextView.text = data.amount
        if (data.cardPan.isNotEmpty()) {
            cardImageView.setImageResource(data.cardPan.getCardType().cardIcon)
            cardNumberTextView.text = data.cardPan.maskedCardPan()
        }
        button.setText(R.string.go_to_main)
        button.setOnClickListener {
            backToRoot()
        }
    }

    private fun showShimmer() = with(binding) {
        paymentContainer.isVisible = false
        statusContainer.isVisible = false
        button.isVisible = false
        shimmer.root.isVisible = true
    }

    private fun showStatusContainer() = with(binding) {
        statusContainer.isVisible = true
        paymentContainer.isVisible = false
        button.isVisible = true
        shimmer.root.isVisible = false
    }

    private fun showPaymentContainer() = with(binding) {
        statusContainer.isVisible = false
        paymentContainer.isVisible = true
        button.isVisible = true
        shimmer.root.isVisible = false
    }
}
