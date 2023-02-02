package com.qr.pos.amper.utils.validation.rule

import com.qr.pos.amper.utils.validation.ValidatorRule

class RequiredRule(message: String): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
       return value.isNotEmpty()
    }
}
