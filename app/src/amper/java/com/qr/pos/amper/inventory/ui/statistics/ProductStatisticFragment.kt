package com.qr.pos.amper.inventory.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.Distribution.BucketOptions.Linear
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.FragmentProductStatisticBinding
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.ui.statistics.adapter.ContentAdapter
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductStatisticFragment : BaseFragment<FragmentProductStatisticBinding>(R.layout.fragment_product_statistic) {

    override val binding by viewBinding(FragmentProductStatisticBinding::bind)

    private val viewModel by viewModels<ProductStatsViewModel>()

    private val adapter = ContentAdapter()

    private val args by navArgs<ProductStatisticFragmentArgs>()

    override fun setupView() {
        with(binding){
            productRecycler.adapter = adapter
            productRecycler.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun setupListener() {
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Loading -> showLoading(event.isLoading)
                is UiEvent.Error -> showErrorScreen(event.message)
                is UiEvent.NullOrEmpty -> showProductNotExist()
                else -> {}
            }
        }

        viewModel.uiState.observe(this){ state ->
            showData(state)
        }
        viewModel.getProductLive(args.product.productCode ?: "")
    }

    private fun showData(state: Product?) {
        state?.let {
            val list = mutableListOf<Pair<String, String>>()
            list.add(Pair(getString(R.string.product_id_label), it.productCode ?: ""))
            list.add(Pair(getString(R.string.product_description_label_stats), it.description ?: ""))
            list.add(Pair("Size:", it.size ?: ""))
            list.add(Pair("Quantity:", (it.quantity ?: 0).toString()))
            list.add(Pair("Quantity Sold:", (it.quantitySold ?: 0).toString()))
            list.add(Pair("Price:", getString(R.string.price_format, (it.price ?: 0).toString())))
            list.add(Pair("Total Amount Sold:", getString(R.string.price_format, it.computeTotalSales().toString() )))
            adapter.setData(list)
        }
    }

    private fun showProductNotExist() {
        alertDialog.setTitle(getString(R.string.dialog_error_title))
            .setMessage("The product is not existing anymore.")
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d, _ ->
                d.dismiss()
                navController?.popBackStack(R.id.inventoryFragment, false)
            }.create().show()
    }

}