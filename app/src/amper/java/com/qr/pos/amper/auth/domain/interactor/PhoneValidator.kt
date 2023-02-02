package com.qr.pos.amper.auth.domain.interactor

import android.util.Patterns
import com.qr.pos.amper.utils.LengthTypes
import com.qr.pos.amper.utils.validation.FieldValidation
import com.qr.pos.amper.utils.validation.Validator
import com.qr.pos.amper.utils.validation.rule.LengthRangeRule
import com.qr.pos.amper.utils.validation.rule.RequiredRule
import com.qr.pos.amper.utils.validation.rule.ValidEmailRule
import com.qr.pos.amper.utils.validation.rule.ValidPhoneRule
import com.qr.pos.amper.utils.validation.withRules
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PhoneValidator @Inject constructor() {
    private val validator = Validator()
    operator fun invoke(value: String, field: String): FieldValidation {
        validator.withRules(
            RequiredRule("$field field is empty."),
            LengthRangeRule("$field must be ${LengthTypes.PHONE_LENGTH.min} to ${LengthTypes.PHONE_LENGTH.max} characters", LengthTypes.PHONE_LENGTH.key),
            ValidPhoneRule("$field is not valid")
        )
        return validator.validate(value).result
    }
}