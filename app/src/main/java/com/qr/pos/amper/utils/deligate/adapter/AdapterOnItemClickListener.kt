package com.qr.pos.amper.utils.deligate.adapter

interface AdapterOnItemClickListener<T> {
    fun onItemClick(data: T, position: Int)
}