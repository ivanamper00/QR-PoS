package com.qr.pos.amper.utils.validation.rule

import com.qr.pos.amper.utils.validation.ValidatorRule

class EqualRule(message: String, private val compare: String): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
        return value == compare
    }
}