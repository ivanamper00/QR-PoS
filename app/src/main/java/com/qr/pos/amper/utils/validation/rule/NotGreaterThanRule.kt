package com.qr.pos.amper.utils.validation.rule

import com.qr.pos.amper.utils.validation.ValidatorRule

class NotGreaterThanRule(message: String, private val compare: Double): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
        return (value.toDoubleOrNull() ?: 0.00) >= compare
    }
}