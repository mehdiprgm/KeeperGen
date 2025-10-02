package org.zendev.keepergen.dialog

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import org.zendev.keepergen.R
import org.zendev.keepergen.tools.preferencesName
import kotlin.coroutines.resume
import androidx.core.content.edit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.generatePassword
import org.zendev.keepergen.tools.getPasswordStrength
import org.zendev.keepergen.tools.resizeTextViewDrawable
import org.zendev.keepergen.tools.startDialogAnimation

class Dialogs {

    companion object {

        private fun createDialog(context: Context, layoutFile: Int): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(layoutFile)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dialog.window?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    context, R.drawable.dialog_background
                )
            )
            dialog.setCancelable(false)

            return dialog
        }

        suspend fun registerPasscode(context: Context): Boolean =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_register_passcode)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val txtPasscode = dialog.findViewById<TextInputEditText>(R.id.txtPasscode)
                val txtLayPasscode = dialog.findViewById<TextInputLayout>(R.id.txtLayPasscode)

                val btnRegister = dialog.findViewById<Button>(R.id.btnRegister)
                btnRegister.setOnClickListener {
                    if (txtPasscode.text.toString().isEmpty()) {
                        txtLayPasscode.error = "Passcode is empty."
                        txtLayPasscode.isErrorEnabled = true
                    } else {
                        txtLayPasscode.isErrorEnabled = false

                        /* update the preferences */
                        val pref =
                            context.getSharedPreferences(preferencesName, MODE_PRIVATE)

                        pref.edit {
                            putBoolean("Registered", true)
                            putString("Passcode", txtPasscode.text.toString())
                        }

                        continuation.resume(true)
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }

        suspend fun ask(
            context: Context,
            icon: Int,
            title: String,
            message: String,
            cancellable: Boolean = false
        ): Boolean =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_ask)
                dialog.setCancelable(cancellable)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)

                tvTitle.text = title
                tvMessage.text = message

                dialog.findViewById<ImageView>(R.id.imgIcon).setImageDrawable(
                    ContextCompat.getDrawable(context, icon)
                )

                dialog.findViewById<Button>(R.id.btnNo).setOnClickListener {
                    continuation.resume(false)
                    dialog.dismiss()
                }

                dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
                    continuation.resume(true)
                    dialog.dismiss()
                }

                dialog.setOnCancelListener {
                    continuation.resume(false)
                }

                dialog.show()
                continuation.invokeOnCancellation {
                    dialog.dismiss()
                }
            }


        suspend fun textInput(
            context: Context,
            title: String,
            message: String,
            hint: String,
            defaultText: String = "",
            isPassword: Boolean = false,
            isNumber: Boolean = false,
            cancellable: Boolean = false
        ): String = suspendCancellableCoroutine { continuation ->
            val dialog = createDialog(context, R.layout.dialog_text_input)
            dialog.setCancelable(cancellable)
            startDialogAnimation(dialog.findViewById(R.id.main))

            val txtInput = dialog.findViewById<EditText>(R.id.txtInput)

            dialog.findViewById<TextView>(R.id.tvTitle).text = title
            dialog.findViewById<TextView>(R.id.tvMessage).text = message

            txtInput.hint = hint
            txtInput.setText(defaultText)

            if (isPassword) {
                if (isNumber) {
                    txtInput.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                } else {
                    txtInput.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                txtInput.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                if (isNumber) {
                    txtInput.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    txtInput.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

            dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
                val text = txtInput.text.toString()
                continuation.resume(text)
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                continuation.resume("")
                dialog.dismiss()
            }

            dialog.setOnCancelListener {
                continuation.resume("")
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun newPassword(context: Context) {
            val dialog = createDialog(context, R.layout.dialog_new_password)
            dialog.setCancelable(true)
            startDialogAnimation(dialog.findViewById(R.id.main))

            val btnGeneratePassword = dialog.findViewById<Button>(R.id.btnGeneratePassword)
            val btnCopyPassword = dialog.findViewById<FloatingActionButton>(R.id.btnCopyPassword)

            val tvPassword = dialog.findViewById<TextView>(R.id.tvPassword)
            val tvLength = dialog.findViewById<TextView>(R.id.tvLength)

            val seekLength = dialog.findViewById<SeekBar>(R.id.seekLength)
            val rbPasswordStrength = dialog.findViewById<RatingBar>(R.id.rbPasswordStrength)

            val chkNumbers = dialog.findViewById<SwitchMaterial>(R.id.switchNumbers)
            val chkSymbols = dialog.findViewById<SwitchMaterial>(R.id.switchSymbols)
            val chkLowerCase = dialog.findViewById<SwitchMaterial>(R.id.switchLowerCase)

            btnGeneratePassword.setOnClickListener {
                val password = generatePassword(
                    seekLength.progress,
                    chkNumbers.isChecked,
                    chkSymbols.isChecked,
                    chkLowerCase.isChecked
                )

                val strength = getPasswordStrength(password)

                tvPassword.text = password
                rbPasswordStrength.rating = strength.toFloat()
            }

            btnCopyPassword.setOnClickListener {
                copyTextToClipboard(context, "Password", tvPassword.text.toString())
            }

            seekLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                // This method is called when the progress value of the SeekBar changes.
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    tvLength.text = "Password Length   $progress"
                }

                // This method is called when the user starts to touch and drag the SeekBar thumb.
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // You might use this to show a tooltip or perform a pre-drag action.
                }

                // This method is called when the user stops touching and dragging the SeekBar thumb.
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // This is a good place to perform a final action, like saving the selected value
                    // or triggering an event based on the final progress.
                }
            })

            dialog.show()
        }

        fun load(context: Context, title: String, message: String): Dialog {
            val dialog = createDialog(context, R.layout.dialog_load)
            startDialogAnimation(dialog.findViewById(R.id.main))

            dialog.findViewById<TextView>(R.id.tvTitle).text = title
            dialog.findViewById<TextView>(R.id.tvMessage).text = message

            return dialog
        }

        fun confirm(context: Context, title: String, message: String, dialogType: DialogType) {
            val dialog = createDialog(context, R.layout.dialog_confirm)
            startDialogAnimation(dialog.findViewById(R.id.main))

            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)

            tvTitle.text = title
            tvMessage.text = message

            when (dialogType) {
                DialogType.Error -> {
                    resizeTextViewDrawable(context, tvTitle, R.drawable.ic_error, 22)
                }

                DialogType.Warning -> {
                    resizeTextViewDrawable(context, tvTitle, R.drawable.ic_warning, 22)
                }

                DialogType.Information -> {
                    resizeTextViewDrawable(context, tvTitle, R.drawable.ic_info, 22)
                }
            }

            dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun exception(context: Context, exception: Exception) {
            val message = "${exception.message}}"
            confirm(context, title = "Error", message = message, DialogType.Error)
        }
    }
}