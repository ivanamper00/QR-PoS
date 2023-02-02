package com.qr.pos.amper.inventory.ui.item_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.ItemInventoryProductListBinding
import com.qr.pos.amper.inventory.data.dto.Product

class InventoryListAdapter(
    private val listener: ProductOptionListener
): Adapter<InventoryListAdapter.Holder>() {

    private var products: List<Product> = emptyList()

    private var searchCopy: List<Product> = emptyList()

    class Holder(itemView: View): ViewHolder(itemView) {
        val binding by lazy { ItemInventoryProductListBinding.bind(itemView) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_product_list, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder.binding){
            val product = products[position]
            editButton.setOnClickListener { listener.onProductEdit(product) }
            deleteButton.setOnClickListener {
                listener.onProductDelete(product)
            }
            root.setOnClickListener {
                listener.onItemSelected(product)
            }
            productCode.text = product.productCode
            productDesc.text = product.description
        }
    }

    override fun getItemCount(): Int = products.size

    fun setData(products: List<Product>){
        this.products = products
        this.searchCopy = products
        notifyDataSetChanged()
    }

    fun search(key : String){
        this.products = this.searchCopy.filter {
            Gson().toJson(it).contains(key, true)
        }
        notifyDataSetChanged()
    }

    interface ProductOptionListener {
        fun onProductDelete(product: Product)
        fun onProductEdit(product: Product)
        fun onItemSelected(product: Product)
    }
}