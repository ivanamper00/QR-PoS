package com.qr.pos.amper.auth.domain.interactor

import android.util.Patterns
import com.qr.pos.amper.utils.validation.FieldValidation
import com.qr.pos.amper.utils.validation.Validator
import com.qr.pos.amper.utils.validation.rule.EqualRule
import com.qr.pos.amper.utils.validation.rule.RequiredRule
import com.qr.pos.amper.utils.validation.rule.ValidEmailRule
import com.qr.pos.amper.utils.validation.withRules
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ConfirmPassValidator @Inject constructor() {
    private val validator = Validator()
    operator fun invoke(confirm: String, pass: String, field: String): FieldValidation {
        validator.withRules(
            RequiredRule("$field field is empty."),
            EqualRule("$field is not match.", pass)
        )
        return validator.validate(confirm).result
    }
}