package com.qr.pos.amper.inventory.ui.statistics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.ItemProductFieldBinding

class ContentAdapter: Adapter<ContentAdapter.Holder>() {

    private var mapList: List<Pair<String, String>> = emptyList()

    class Holder(itemView: View): ViewHolder(itemView) {
        val binding by lazy { ItemProductFieldBinding.bind(itemView) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_field, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder.binding){
            val map = mapList[position]
            fieldTextView.text = map.first
            valueTextView.text = map.second
        }
    }

    override fun getItemCount(): Int = mapList.size

    fun setData(mapList: List<Pair<String, String>>){
        this.mapList = mapList
        notifyDataSetChanged()
    }
}