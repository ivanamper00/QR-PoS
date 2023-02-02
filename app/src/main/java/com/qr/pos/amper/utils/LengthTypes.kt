package com.qr.pos.amper.utils

enum class LengthTypes(val key: Int, val min: Int, val max: Int) {
    PHONE_LENGTH(90, 11, 13),
    TEXT_FIELD_LENGTH(91, 0, 50),
    PASSWORD(92, 8, 25);

    companion object {
        fun fromTypeKey(key: Int): LengthTypes {
            return values().find { it.key == key } ?: TEXT_FIELD_LENGTH
        }
    }
}