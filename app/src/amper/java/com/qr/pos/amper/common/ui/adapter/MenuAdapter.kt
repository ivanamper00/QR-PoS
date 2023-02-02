package com.qr.pos.amper.common.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qr.pos.amper.R
import com.qr.pos.amper.common.ui.DashboardActions
import com.qr.pos.amper.databinding.ItemButtonOptionsBinding
import com.qr.pos.amper.utils.deligate.adapter.AdapterOnItemClickListener

class MenuAdapter(
    private val listener: AdapterOnItemClickListener<DashboardActions>,
    private val menus: List<DashboardActions>
): Adapter<MenuAdapter.Holder>() {

    class Holder(itemView: View): ViewHolder(itemView) {
        val binding by lazy { ItemButtonOptionsBinding.bind(itemView) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button_options,parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val action = menus[position]
        with(holder.binding){
            menuImage.setImageResource(action.image)
            menuText.text = action.alias
            root.setOnClickListener {
                listener.onItemClick(action, position)
            }
        }
    }

    override fun getItemCount(): Int = menus.size
}