package com.qr.pos.amper.utils.validation.rule

import android.util.Patterns
import com.qr.pos.amper.utils.validation.ValidatorRule
import java.util.regex.Pattern

class ValidPasswordRule(message: String): ValidatorRule(message) {

    override fun validate(value: String): Boolean {
        return PASSWORD_PATTER.matcher(value).matches()
    }

    companion object {
        val PASSWORD_PATTER: Pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-])")
    }

}
