package com.qr.pos.amper.auth.utils

enum class UserType(val key: Int, val alias: String) {
    ADMIN(0, "Admin"),
    HEAD(3, "Head"),
    CASHIER(1, "Cashier"),
    INVENTORY(2, "Inventory");

    companion object {
        fun fromTypeKey(key: Int): UserType {
            return values().find { it.key == key } ?: CASHIER
        }
        fun fromTypeAlias(alias: String): UserType{
            return values().find { it.alias == alias } ?: CASHIER
        }
    }
}