package com.qr.pos.amper.utils.validation
class Validator {
    private var rules = arrayListOf<ValidatorRule>()

    fun addRule(rule: ValidatorRule) = rules.add(rule)

    fun validate(value: String): ValidatorResult {
        val result = arrayListOf<ValidatorItem>()

        rules.forEach {
            result.add(ValidatorItem(it, it.validate(value)))
        }

        return ValidatorResult(result)
    }
}

fun Validator.withRules(vararg rules: ValidatorRule): Validator {
    rules.forEach {
        addRule(it)
    }
    return this
}
