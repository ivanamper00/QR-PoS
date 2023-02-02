package com.qr.pos.amper.inventory.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var productCode: String? = null,
    var description: String? = null,
    var category: String? = null,
    var color: String? = null,
    var size: String? = null,
    var price: Double? = null,
    val quantity: Int? = null,
    val quantitySold: Int? = null,
    val visible: Boolean? = null
) : Parcelable {
    fun setPriceString(price: String){
        try {
            this.price = price.toDouble()
        }catch (_: Exception){}
    }

    fun getPriceString(): String = if(price == null) "0.00" else price.toString()

    fun computeTotalSales() : Double {
        return ((quantitySold ?: 0).toDouble() * (price ?: 0.00))
    }
}
