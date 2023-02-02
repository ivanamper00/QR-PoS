package com.qr.pos.amper.utils.validation.rule

import android.util.Patterns
import com.qr.pos.amper.utils.validation.ValidatorRule

class ValidEmailRule(message: String): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

}
