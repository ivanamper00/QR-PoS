package com.qr.pos.amper.cashier.domain.interactor

import com.qr.pos.amper.utils.validation.FieldValidation
import com.qr.pos.amper.utils.validation.Validator
import com.qr.pos.amper.utils.validation.rule.NotGreaterThanRule
import com.qr.pos.amper.utils.validation.rule.RequiredRule
import com.qr.pos.amper.utils.validation.withRules
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PaymentValidator @Inject constructor() {
    private val validator = Validator()
    operator fun invoke(value: String, compare: Double, field: String): FieldValidation {
        validator.withRules(
            RequiredRule("$field field is required."),
            NotGreaterThanRule("$field should not be greater than $compare", compare)
        )
        return validator.validate(value).result
    }
}