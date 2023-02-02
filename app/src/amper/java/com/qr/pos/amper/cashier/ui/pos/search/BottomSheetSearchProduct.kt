package com.qr.pos.amper.cashier.ui.pos.search

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qr.pos.amper.R
import com.qr.pos.amper.cashier.ui.pos.search.adapter.ProductDetailsAdapter
import com.qr.pos.amper.databinding.BottomSheetDialogSearchProductBinding
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.deligate.UIEventInterface
import com.qr.pos.amper.utils.deligate.adapter.AdapterOnItemClickListener
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseBottomSheetFragment
import com.qr.pos.amper.utils.extensions.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class BottomSheetSearchProduct(
    listener: AdapterOnItemClickListener<Product>,
    private val uiListener: UIEventInterface
): BaseBottomSheetFragment<BottomSheetDialogSearchProductBinding>(R.layout.bottom_sheet_dialog_search_product) {

    private val adapter = ProductDetailsAdapter(listener)

    private val viewModel by viewModels<ProductSearchViewModel>()

    override val binding: BottomSheetDialogSearchProductBinding get() = BottomSheetDialogSearchProductBinding.bind(requireView())

    override fun setupView() {
        viewModel.getProducts()
        with(binding){
            productRecycler.adapter = adapter
            productRecycler.layoutManager = LinearLayoutManager(requireContext())
            searchProductEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    adapter.search(it.toString())
                }.launchIn(lifecycleScope)
        }
    }

    override fun setupListener() {
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Error -> uiListener.showErrorScreen(event.message)
                is UiEvent.Loading -> uiListener.showLoading(event.isLoading)
                else -> {}
            }
        }

        viewModel.uiState.observe(this){ state ->
            adapter.setData(state)
        }

    }

}