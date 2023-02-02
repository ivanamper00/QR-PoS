package com.qr.pos.amper.inventory.ui.add_product

import com.qr.pos.amper.auth.domain.interactor.RequiredValidator
import com.qr.pos.amper.utils.validation.FieldValidation

data class AddProductState(
    val descValidation: FieldValidation? = null,
    val categoryValidation: FieldValidation? = null,
    val sizeValidation: FieldValidation? = null,
    val priceValidation: FieldValidation? = null,
    val productCode: String? = null
){
    val isButtonEnabled: Boolean get() =
            descValidation?.passed == true &&
            categoryValidation?.passed == true &&
            sizeValidation?.passed == true &&
            priceValidation?.passed == true
}