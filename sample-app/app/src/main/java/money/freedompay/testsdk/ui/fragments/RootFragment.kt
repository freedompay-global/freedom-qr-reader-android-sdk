package money.freedompay.testsdk.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import money.freedompay.qrreader.api.models.responses.TokenizedCardDetailResponse
import money.freedompay.testsdk.App
import money.freedompay.testsdk.R
import money.freedompay.testsdk.databinding.RootFragmentBinding
import money.freedompay.testsdk.extencions.replaceFragment
import money.freedompay.testsdk.model.RequestResult
import money.freedompay.testsdk.ui.adapters.CardsAdapter
import money.freedompay.testsdk.utils.SwipeToDeleteCallback
import money.freedompay.testsdk.wrapper.FreedomSdkWrapper

class RootFragment : CoreFragment(R.layout.root_fragment) {

    companion object {
        fun create() = RootFragment()
    }

    private lateinit var sdk: FreedomSdkWrapper

    private var vibrator: Vibrator? = null

    private val cardsAdapter = CardsAdapter()
    private val itemTouchHelper by lazy {
        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), ::removeCard))
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openQRScanner()
        } else {
            showErrorAlert(R.string.camera_error)
        }
    }

    private lateinit var binding: RootFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RootFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sdk = (activity?.application as App).getFreedomSdkWrapper()
        vibrator = ContextCompat.getSystemService(requireContext(), Vibrator::class.java)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        loadCards()
    }

    private fun initViews() = with(binding) {
        itemTouchHelper.attachToRecyclerView(cards)
        cards.adapter = cardsAdapter
        buttonInitPayment.setOnClickListener {
            requestCameraPermission(requireActivity())
        }
        addCardViewButton.setOnClickListener {
            replaceFragment(AddCardFragment.create())
        }
    }

    private fun requestCameraPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openQRScanner()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openQRScanner() {
        replaceFragment(QRScannerFragment.create())
    }

    private fun loadCards() {
        lifecycleScope.launch {
            showLoader(true)
            when (val result = sdk.getListCards()) {
                is RequestResult.Success -> {
                    showData(result.data)
                }
                is RequestResult.Error -> {
                    showData(listOf(), R.string.load_cards_error)
                }
                is RequestResult.FatalError -> {
                    showData(listOf(), R.string.load_cards_error)
                }
            }
            showLoader(false)
        }
    }

    private fun removeCard(index: Int) {
        lifecycleScope.launch {
            val processList = cardsAdapter.getData()
            val isRemoved = sdk.removeCard(processList[index].cardToken)
            vibrate()
            if (isRemoved) {
                val newList = processList.apply { removeAt(index) }
                showData(newList)
            } else {
                showErrorAlert(R.string.remove_cards_error) {
                    showData(processList)
                }
            }
        }
    }

    private fun showLoader(isVisible: Boolean) = with(binding) {
        cards.isVisible = !isVisible
        loader.isVisible = isVisible
    }

    private fun showData(list: List<TokenizedCardDetailResponse>, messageError: Int? = null) {
        binding.emptyListMessage.setText(messageError ?: R.string.empty_cards)
        binding.emptyListMessage.isVisible = list.isEmpty()
        cardsAdapter.setData(list)
    }

    private fun showErrorAlert(message: Int, onDismiss: (() -> Unit)? = null) {
        lifecycleScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(R.string.it_is_clear) { dialog, _ ->
                    dialog.dismiss()
                }.setOnDismissListener {
                    onDismiss?.invoke()
                }.create().show()
        }
    }

    private fun vibrate() {
        vibrator?.let { vibrator ->
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 100))
                } else {
                    vibrator.vibrate(100)
                }
            }
        }
    }
}
