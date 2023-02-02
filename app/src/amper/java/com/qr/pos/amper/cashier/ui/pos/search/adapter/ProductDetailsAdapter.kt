package com.qr.pos.amper.cashier.ui.pos.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.ItemProductDetailsSearchBinding
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.deligate.adapter.AdapterOnItemClickListener

class ProductDetailsAdapter(
    private val listener: AdapterOnItemClickListener<Product>
): Adapter<ProductDetailsAdapter.Holder>() {
    class Holder(itemView: View): ViewHolder(itemView) {
        val binding by lazy { ItemProductDetailsSearchBinding.bind(itemView) }
    }

    private var products = emptyList<Product>()
    private var searchItem = emptyList<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_details_search, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = products[position]
        with(holder.binding){
            productDescText.text = product.description
            productPriceText.text = root.context.getString(R.string.price_format, String.format("%.2f", product.price ?: 0.00))
            productIDText.text = product.productCode
            root.setOnClickListener {
                listener.onItemClick(product, position)
            }
        }
    }

    override fun getItemCount(): Int = products.size

    fun setData(products: List<Product>){
        this.products = products
        this.searchItem = products
        notifyDataSetChanged()
    }

    fun search(key: String){
        products = searchItem.filter {
            Gson().toJson(it).contains(key, true)
        }
        notifyDataSetChanged()
    }
}