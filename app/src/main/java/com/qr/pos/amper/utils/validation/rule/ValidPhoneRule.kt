package com.qr.pos.amper.utils.validation.rule

import android.util.Patterns
import com.qr.pos.amper.utils.validation.ValidatorRule

class ValidPhoneRule(message: String): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
        return Patterns.PHONE.matcher(value).matches()
    }

}
