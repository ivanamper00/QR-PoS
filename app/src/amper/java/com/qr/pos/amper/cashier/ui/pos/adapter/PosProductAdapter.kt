package com.qr.pos.amper.cashier.ui.pos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qr.pos.amper.R
import com.qr.pos.amper.cashier.data.dto.PosPair
import com.qr.pos.amper.databinding.ItemPosProductBinding
import com.qr.pos.amper.inventory.data.dto.Product

class PosProductAdapter: Adapter<PosProductAdapter.Holder>() {

    var products = emptyList<PosPair<String?, List<Product>>>()

    class Holder(itemView: View): ViewHolder(itemView) {
        val binding by lazy { ItemPosProductBinding.bind(itemView) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pos_product, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = products[position]
        with(holder.binding){
            fieldTextView.text = root.context.resources.getQuantityString(
                R.plurals.description_and_quantity,
                product.second?.size ?: 0,
                product.second?.first()?.description,
                product.second?.size
            )
            val amount = (product.second?.size ?: 0) * (product.second?.first()?.price ?: 0.00)
            valueTextView.text = root.context.getString(R.string.price_format, String.format("%.2f", amount))
        }
    }

    override fun getItemCount(): Int = products.size

    fun setData(products: List<PosPair<String?, List<Product>>>){
        this.products = products
        notifyDataSetChanged()
    }
}