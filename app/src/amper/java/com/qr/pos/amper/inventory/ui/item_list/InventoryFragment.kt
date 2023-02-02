package com.qr.pos.amper.inventory.ui.item_list

import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.fragment.BaseFragment
import com.qr.pos.amper.databinding.FragmentInventoryBinding
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.ui.item_list.adapter.InventoryListAdapter
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.extensions.goneView
import com.qr.pos.amper.utils.extensions.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class InventoryFragment : BaseFragment<FragmentInventoryBinding>(R.layout.fragment_inventory), InventoryListAdapter.ProductOptionListener {

    override val binding by viewBinding(FragmentInventoryBinding::bind)

    private val viewModel by viewModels<InventoryViewModel>()

    private val adapter by lazy { InventoryListAdapter(this) }

    override fun setupView() {
        with(binding){
            productRecycler.layoutManager = LinearLayoutManager(requireContext())
            productRecycler.adapter = adapter
            with(searchProductContainer){
                endIconMode = TextInputLayout.END_ICON_CUSTOM
                endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
                setEndIconOnClickListener {
                    searchProductEditText.setText("")
                }
            }
            searchProductEditText.textChanges()
                .onEach {
                    searchProductContainer.isEndIconVisible = !it.isNullOrEmpty()
                }.drop(1)
                .debounce(300)
                .onEach {
                    adapter.search(it.toString())
                }
                .launchIn(lifecycleScope)
            createProductButton.setOnClickListener {
                navigateToCreateProduct()
            }
        }
    }

    private fun navigateToCreateProduct() {
        navController?.navigate(R.id.inventoryToCreateProduct)
    }

    override fun setupListener() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                when(sf.getUserType()){
                    UserType.INVENTORY -> showLogoutDialog()
                    else -> { requireActivity().finish() }
                }
            }
        })
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Error -> showErrorScreen(event.message)
                is InventoryEvent.ProductDeleted -> showDeleteProductSuccess(event.data)
                is InventoryEvent.UndoDeleteSuccess -> showUndoDeleteSuccess(event.data)
            }
        }

        viewModel.uiState.observe(this){ state ->
            showProducts(state.products)
        }

        viewModel.getProductList()
    }

    private fun showLogoutDialog() {
        alertDialog.setTitle(getString(R.string.signout_dialog_title))
            .setMessage(getString(R.string.signout_dialog_message))
            .setPositiveButton(getString(R.string.yes_dialog_label)){d,_ ->
                d.dismiss()
                logoutUser()
            }.setNegativeButton(getString(R.string.cancel_label)){d, _ ->
                d.dismiss()
            }.create().show()
    }

    private fun showProducts(products: List<Product>?) {
        showEmptyScreen(products.isNullOrEmpty())
        products?.let { adapter.setData(it) }
    }

    private fun showEmptyScreen(show: Boolean) {
        binding.emptyScreen.emptyContainer.goneView(!show)
    }

    private fun navigateToEditProductPage(data: Product) {
        val direction = InventoryFragmentDirections.inventoryToEditProduct(data)
        navController?.navigate(direction)
    }

    private fun navigateToProductStatisticsPage(product: Product) {
        val direction = InventoryFragmentDirections.inventoryToStatistics(product)
        navController?.navigate(direction)
    }

    private fun showDeleteProductSuccess(data: Product) {
        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.product_delete_success, data.productCode),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.undo_label)){
            viewModel.undoDelete(product = data)
        }
        snackbar.show()
    }

    private fun showUndoDeleteSuccess(data: Product){
        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.undo_delete_success, data.productCode),
            Snackbar.LENGTH_SHORT
        )
        snackbar.show()
    }

    override fun onProductDelete(product: Product) {
        viewModel.deleteProduct(product)
    }

    override fun onProductEdit(product: Product) {
        navigateToEditProductPage(product)
    }

    override fun onItemSelected(product: Product) {
        navigateToProductStatisticsPage(product)
    }

}