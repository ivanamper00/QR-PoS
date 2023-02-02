package com.qr.pos.amper.auth.ui.login

import android.content.DialogInterface
import androidx.core.view.isVisible
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.BottomSheetActivationCodeBinding
import com.qr.pos.amper.utils.base.fragment.BaseBottomSheetFragment

class DialogActivationCode(
    private val listener: CodeListener,
    private val buttonText: String,
    private val hintText: String,
    private val title: String? = null,
    private val message: String? = null,
    private val reminder: String? = null
): BaseBottomSheetFragment<BottomSheetActivationCodeBinding>(R.layout.bottom_sheet_activation_code) {

    override val binding: BottomSheetActivationCodeBinding get() = BottomSheetActivationCodeBinding.bind(requireView())

    override fun setupListener() {
        with(binding){
            activateButton.setOnClickListener {
                listener.onCodeSubmit(activationCodeEditText.text.toString().trim())
                dismiss()
                activationCodeEditText.text?.clear()
            }
            activateButton.text = buttonText
            activationContainer.hint = hintText
            titleText.text = title ?: ""
            messageText.text = message ?: ""

            if(reminder.isNullOrEmpty()) reminderText.isVisible = !reminder.isNullOrEmpty()
            else reminderText.text = reminder
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onCancel()
    }

    override fun setupView() { }

    interface CodeListener{
        fun onCodeSubmit(activationCode: String)
        fun onCancel()
    }
}