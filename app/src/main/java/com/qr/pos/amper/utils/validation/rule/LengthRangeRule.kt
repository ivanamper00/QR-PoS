package com.qr.pos.amper.utils.validation.rule

import com.qr.pos.amper.utils.LengthTypes
import com.qr.pos.amper.utils.validation.ValidatorRule

class LengthRangeRule(message: String): ValidatorRule(message) {

    var min: Int = LengthTypes.TEXT_FIELD_LENGTH.min
    var max: Int = LengthTypes.TEXT_FIELD_LENGTH.max

    constructor(message: String, rangeType: Int) : this(message){
        val type = LengthTypes.fromTypeKey(rangeType)
        min = type.min
        max = type.max
    }

    override fun validate(value: String): Boolean {
       return value.length in min..max
    }

}
