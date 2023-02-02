package com.qr.pos.amper.inventory.ui.add_product

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.datamatrix.DataMatrixWriter
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.FragmentAddProductBinding
import com.qr.pos.amper.utils.StringUtils
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseFragment
import com.qr.pos.amper.utils.extensions.textChanges
import com.qr.pos.amper.utils.validation.FieldValidation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AddProductFragment : BaseFragment<FragmentAddProductBinding>(R.layout.fragment_add_product) {

    override val binding by viewBinding(FragmentAddProductBinding::bind)

    private val viewModel by viewModels<AddProductViewModel>()

    override fun setupListener() {
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Error -> showErrorScreen(event.message)
                is UiEvent.Loading -> showLoading(event.isLoading)
                is AddProductEvent.ProductCreationSuccess -> showSuccessCreationDialog()
                is AddProductEvent.ErrorProductCode -> showErrorOnProductCode()
            }
        }

        viewModel.uiState.observe(this){ state ->
            binding.createProductButton.isEnabled = state.isButtonEnabled
            setError(binding.productDescContainer, state.descValidation?.message)
            setError(binding.productCategoryContainer, state.categoryValidation?.message)
            setError(binding.productSizeContainer, state.sizeValidation?.message)
            setError(binding.productPriceContainer, state.priceValidation?.message)
            generateQr(binding.qrCode, state.productCode)
        }

        viewModel.generateCode()
    }

    private fun showSuccessCreationDialog() {
        alertDialog.setTitle(getString(R.string.dialog_product_success_title))
            .setMessage(getString(R.string.dialog_product_created_success_desc))
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d,_ ->
                d.dismiss()
                navController?.popBackStack(R.id.inventoryFragment,false)
            }.create().show()
    }

    private fun showErrorOnProductCode() {
        alertDialog.setTitle(getString(R.string.dialog_error_title))
            .setMessage(getString(R.string.dialog_create_product_error_desc))
            .setPositiveButton(getString(R.string.dialog_retry_label)){ d, _ ->
                d.dismiss()
                viewModel.generateCode()
            }.setNegativeButton(getString(R.string.cancel_label)){ d, _ ->
                d.dismiss()
                navController?.popBackStack(R.id.inventoryFragment,false)
            }
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
            product = viewModel.product
            productDescEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach { viewModel.validateDesc(it.toString(), getString(R.string.product_description_label)) }
                .launchIn(lifecycleScope)

            productCategoryEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach { viewModel.validateCategory(it.toString(), getString(R.string.product_category_label)) }
                .launchIn(lifecycleScope)

            productSizeEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach { viewModel.validateSize(it.toString(), getString(R.string.product_size_label)) }
                .launchIn(lifecycleScope)

            productPriceEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validatePrice(it.toString(), getString(R.string.product_price_label))
                    productPrice.text = getString(R.string.price_format, it.toString())
                }
                .launchIn(lifecycleScope)

            createProductButton.setOnClickListener {
                viewModel.createProduct()
            }
        }
    }

}