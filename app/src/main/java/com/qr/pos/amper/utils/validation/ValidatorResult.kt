package com.qr.pos.amper.utils.validation

class ValidatorResult(
    val items: List<ValidatorItem>
) {

    val count: Int
        get() = items.size


    val passedItems: List<ValidatorItem>
        get() {
            val items = arrayListOf<ValidatorItem>()
            this.items.forEach{
                if(it.isPassed) items.add(it)
            }
            return items
        }

    private val failedItems: List<ValidatorItem>
        get() {
            val items = arrayListOf<ValidatorItem>()
            this.items.forEach{
                if(!it.isPassed) items.add(it)
            }
            return items
        }

    val failedItemMessage: String
        get() {
            return if(failedItems.isNotEmpty()) failedItems[0].message
            else ""
        }

    val passedCount: Int
        get() = passedItems.size

    val failedCount: Int
        get() = failedItems.size

    val isPassed: Boolean
        get() = failedCount == 0

    val result: FieldValidation
        get() = FieldValidation(isPassed, failedItemMessage)
}
