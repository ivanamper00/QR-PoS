package com.qr.pos.amper.utils.validation

class ValidatorItem(
    val rule: ValidatorRule,
    val isPassed: Boolean
) {
    val message: String
        get() = rule.message
}
