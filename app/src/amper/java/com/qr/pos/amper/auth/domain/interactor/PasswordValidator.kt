package com.qr.pos.amper.auth.domain.interactor

import android.util.Patterns
import com.qr.pos.amper.utils.LengthTypes
import com.qr.pos.amper.utils.validation.FieldValidation
import com.qr.pos.amper.utils.validation.Validator
import com.qr.pos.amper.utils.validation.rule.LengthRangeRule
import com.qr.pos.amper.utils.validation.rule.RequiredRule
import com.qr.pos.amper.utils.validation.rule.ValidEmailRule
import com.qr.pos.amper.utils.validation.withRules
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PasswordValidator @Inject constructor() {
    private val validator = Validator()
    operator fun invoke(email: String, field: String): FieldValidation {
        validator.withRules(
            RequiredRule("$field field is empty."),
            LengthRangeRule("$field must be ${LengthTypes.PASSWORD.min} to ${LengthTypes.PASSWORD.max} characters.", LengthTypes.PASSWORD.key)
        )
        return validator.validate(email).result
    }
}