package com.qr.pos.amper.auth.domain.interactor

import android.util.Patterns
import com.qr.pos.amper.utils.validation.FieldValidation
import com.qr.pos.amper.utils.validation.Validator
import com.qr.pos.amper.utils.validation.rule.RequiredRule
import com.qr.pos.amper.utils.validation.rule.ValidEmailRule
import com.qr.pos.amper.utils.validation.withRules
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class RequiredValidator @Inject constructor() {
    private val validator = Validator()
    operator fun invoke(value: String, field: String): FieldValidation {
        validator.withRules(
            RequiredRule("$field field is required."),
        )
        return validator.validate(value).result
    }
}