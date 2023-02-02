package com.qr.pos.amper.utils.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("app:amount")
fun EditText.amount(amount: Double){
    setText(amount.toString())
}