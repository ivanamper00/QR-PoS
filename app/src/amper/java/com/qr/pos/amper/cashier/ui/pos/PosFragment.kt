package com.qr.pos.amper.cashier.ui.pos

import androidx.activity.OnBackPressedCallback
import androidx.camera.view.PreviewView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.ui.login.DialogActivationCode
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.cashier.ui.pos.adapter.PosProductAdapter
import com.qr.pos.amper.cashier.ui.pos.search.BottomSheetSearchProduct
import com.qr.pos.amper.databinding.FragmentPosBinding
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseScannerFragment
import com.qr.pos.amper.utils.deligate.adapter.AdapterOnItemClickListener
import com.qr.pos.amper.utils.extensions.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PosFragment : BaseScannerFragment<FragmentPosBinding>(R.layout.fragment_pos),
    AdapterOnItemClickListener<Product>, DialogActivationCode.CodeListener {

    override val binding by viewBinding(FragmentPosBinding::bind)

    private val viewModel by viewModels<PosViewModel>()

    private val adapter = PosProductAdapter()

    private val searchBottomSheet by lazy { BottomSheetSearchProduct(this, this) }

    private val dialogVoidCode by lazy {
        DialogActivationCode(
            listener = this,
            title = getString(R.string.void_transaction_dialog_title),
            message = getString(R.string.void_transaction_dialog_message),
            buttonText = getString(R.string.void_transaction_dialog_button_text),
            hintText = getString(R.string.void_transaction_dialog_hint)
        )
    }

    override val cameraPreview: PreviewView
        get() = binding.cameraPreview


    override fun setupListener() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showEndSessionDialog()
            }
        })
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Error -> showErrorScreen(event.message)
                is UiEvent.Loading -> showLoading(event.isLoading)
                is UiEvent.NullOrEmpty -> showProductNotFound()
                is PosEvent.ReviewTransaction -> showReviewTransactionDialog(event.change)
                is UiEvent.Success -> newTransaction()
                is PosEvent.VoidSuccess -> newTransaction()
                is PosEvent.EndSession -> {
                    when(event.userType){
                        UserType.ADMIN -> requireActivity().finish()
                        else -> logoutUser()
                    }
                }
            }
        }
        viewModel.uiState.observe(this){ state ->
            state?.products?.let {
                adapter.setData(it)
            }
            binding.btnVoid.isEnabled = state.isVoidButtonEnabled()
            binding.totalAmountTextView.text = getString(R.string.price_format, String.format("%.2f", state?.amount ?: 0.00))
            binding.btnPay.isEnabled = state.isPaymentButtonEnabled()
        }
    }

    private fun showEndSessionDialog() {
        if(isSessionEmpty()) {
            alertDialog.setTitle("End Session")
                .setMessage("Do you want to end session?")
                .setNegativeButton(R.string.cancel_label) { d, _ -> d.dismiss() }
                .setPositiveButton(R.string.dialog_ok_label) { d, _ ->
                    d.dismiss()
                    viewModel.endCashierSession()
                }.create().show()
        } else {
            alertDialog.setTitle("End Session")
                .setMessage("Finish the current transaction first before you end the session")
                .setPositiveButton(R.string.dialog_ok_label) { d, _ ->
                    d.dismiss()
                }.create().show()
        }
    }

    private fun newTransaction() {
        binding.edtAmountPaid.setText("")
        viewModel.newTransaction()
    }

    private fun isSessionEmpty() = viewModel.isSessionEmpty()

    private fun showReviewTransactionDialog(change: Double) {
        alertDialog.setTitle(getString(R.string.change_dialog_title))
            .setMessage(getString(R.string.price_format, String.format("%.2f", change)))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel_label)){ d, _ ->
                d.dismiss()
            }
            .setPositiveButton(R.string.dialog_ok_label){ d, _ ->
                viewModel.saveTransaction()
                d.dismiss()
            }.create().show()
    }

    override fun showLoading(isLoading: Boolean) {
        super.showLoading(isLoading)
        if(isLoading){
            stopCamera()
        }else startCamera()
    }

    override fun setupView() {
        super.setupView()
        with(binding){
            productRecycler.adapter = adapter
            productRecycler.layoutManager = LinearLayoutManager(requireContext())

            btnPay.setOnClickListener {
                viewModel.calculate()
            }
            btnSearch.setOnClickListener {
                showSearchDialog()
            }
            btnEndSession.setOnClickListener {
                showEndSessionDialog()
            }
            btnVoid.setOnClickListener {
                showVoidDialog()
            }

            edtAmountPaid.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validateAmount(it.toString(), getString(R.string.amount_paid_label))
                }.launchIn(lifecycleScope)
        }
    }

    private fun showVoidDialog() {
        dialogVoidCode.show(childFragmentManager, DialogActivationCode::class.java.simpleName)
    }

    private fun showSearchDialog() {
        searchBottomSheet.show(childFragmentManager, BottomSheetSearchProduct::class.java.simpleName)
    }

    private fun showProductNotFound(){
        Snackbar.make(binding.root, "Item not found", Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onQRFound(value: String) {
        viewModel.getProduct(value)
    }

    override fun onItemClick(data: Product, position: Int) {
        viewModel.addProduct(data)
        searchBottomSheet.dismiss()
    }

    override fun onCodeSubmit(activationCode: String) {
        viewModel.verifyVoidCode(activationCode)
    }

    override fun onCancel() { }

}