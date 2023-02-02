package com.qr.pos.amper.utils.validation

abstract class ValidatorRule(val message: String) {
    abstract fun validate(value: String): Boolean
}
