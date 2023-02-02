package com.qr.pos.amper.common.ui

import com.qr.pos.amper.BuildConfig
import com.qr.pos.amper.R

enum class DashboardActions(
    val alias: String,
    val key: Int,
    val image: Int,
    val isEnable: Boolean,
) {
    POS (
        alias = "Point of Sales",
        image = R.drawable.ic_pos,
        isEnable = BuildConfig.ENABLE_POS,
        key = 100
    ),
    INVENTORY (
        alias = "Inventory",
        image = R.drawable.ic_inventory,
        isEnable = BuildConfig.ENABLE_INVENTORY,
        key = 101
    ),
    SALES (
        alias = "Sales",
        image = R.drawable.ic_sales,
        isEnable = BuildConfig.ENABLE_SALES,
        key = 102
    ),
    ACCOUNTING(
        alias = "Accounting",
        image = R.drawable.ic_accounting,
        isEnable = true,
        key = 104
    ),
    USER_CREATION (
        alias = "Create User",
        image = R.drawable.ic_register,
        isEnable = BuildConfig.ENABLE_REGISTER_USER,
        key = 103
    )
}
