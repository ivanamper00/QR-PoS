package com.qr.pos.amper.inventory.ui.edit_product

import android.Manifest
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.FragmentAddProductBinding
import com.qr.pos.amper.inventory.ui.add_product.AddProductViewModel
import com.qr.pos.amper.utils.StringUtils
import com.qr.pos.amper.utils.ViewUtils
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseFragment
import com.qr.pos.amper.utils.extensions.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditProductFragment: BaseFragment<FragmentAddProductBinding>(R.layout.fragment_add_product) {

    override val binding by viewBinding(FragmentAddProductBinding::bind)

    private val viewModel by viewModels<AddProductViewModel>()

    private val args by navArgs<EditProductFragmentArgs>()

    private val storagePermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted) downloadQR()
    }

    private fun downloadQR() {
        val product = binding.product!!
        val productName = "${product.description} ${product.productCode}".trim().replace(" ", "_")
        ViewUtils.downloadView(binding.qrContainer, productName){ error ->
            if(error == null) showSuccessDownloadQR()
            else showErrorScreen(error.message ?: "")
        }
    }

    private fun showSuccessDownloadQR() {
        alertDialog.setTitle("QR Downloaded")
            .setMessage("Please check your pictures directory.")
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d,_ ->
                d.dismiss()
            }.create().show()
    }

    override fun setupListener() {
        viewModel.uiEvent.observe(this){event ->
            when(event){
                is UiEvent.Error -> showErrorScreen(event.message)
                is UiEvent.Loading -> showLoading(event.isLoading)
                is EditProductEvent.UpdateSuccess -> showSuccessUpdateDialog()
            }
        }
        viewModel.uiState.observe(this){ state ->
            binding.createProductButton.isEnabled = state.isButtonEnabled
            setError(binding.productDescContainer, state.descValidation?.message)
            setError(binding.productCategoryContainer, state.categoryValidation?.message)
            setError(binding.productSizeContainer, state.sizeValidation?.message)
            setError(binding.productPriceContainer, state.priceValidation?.message)
        }
    }

    private fun showSuccessUpdateDialog() {
        alertDialog.setTitle(getString(R.string.dialog_product_update_success_title))
            .setMessage(getString(R.string.dialog_product_updated_success_desc))
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d,_ ->
                d.dismiss()
                navController?.popBackStack(R.id.inventoryFragment,false)
            }.create().show()
    }

    private fun generateQr(imageView: ImageView, productCode: String?) {
        StringUtils.generateBitmap(productCode)?.let {
            imageView.setImageBitmap(it)
        }
    }

    private fun setError(container: TextInputLayout, message: String?) {
        container.error = message
    }

    override fun setupView() {
        with(binding){
            viewModel.product = args.product
            generateQr(binding.qrCode, viewModel.product.productCode)
            product = viewModel.product

            downloadQrBtn.setOnClickListener {
                if(ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadQR()
                }else storagePermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            productDescEditText.textChanges()
                .debounce(300)
                .onEach { viewModel.validateDesc(it.toString(), getString(R.string.product_description_label)) }
                .launchIn(lifecycleScope)

            productCategoryEditText.textChanges()
                .debounce(300)
                .onEach { viewModel.validateCategory(it.toString(), getString(R.string.product_category_label)) }
                .launchIn(lifecycleScope)

            productSizeEditText.textChanges()
                .debounce(300)
                .onEach { viewModel.validateSize(it.toString(), getString(R.string.product_size_label)) }
                .launchIn(lifecycleScope)

            productPriceEditText.textChanges()
                .debounce(300)
                .onEach {
                    viewModel.validatePrice(it.toString(), getString(R.string.product_price_label))
                    productPrice.text = getString(R.string.price_format, it.toString())
                }
                .launchIn(lifecycleScope)

            createProductButton.text = getString(R.string.update_label)
            createProductButton.setOnClickListener {
                viewModel.updateProduct()
            }
        }
    }
}