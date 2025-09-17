package org.zendev.keepergen.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import org.zendev.keepergen.R
import org.zendev.keepergen.tools.preferencesName
import kotlin.coroutines.resume
import androidx.core.content.edit
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.generatePassword
import org.zendev.keepergen.tools.getDate
import org.zendev.keepergen.tools.getPasswordStrength
import org.zendev.keepergen.tools.setEditTextLimiter
import org.zendev.keepergen.tools.startDialogAnimation
import org.zendev.keepergen.tools.validateEditTextData

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

        private fun isAccountInformationValid(dialog: Dialog): Boolean {
            val txtName = dialog.findViewById<EditText>(R.id.txtName)
            val txtUsername = dialog.findViewById<EditText>(R.id.txtUsername)
            val txtPassword = dialog.findViewById<EditText>(R.id.txtPassword)
            val txtPhoneNumber = dialog.findViewById<EditText>(R.id.txtPhoneNumber)

            var score = 4

            score += validateEditTextData(
                txtName, txtName.text.toString().isEmpty(), "Name is empty"
            )

            score += validateEditTextData(
                txtUsername, txtUsername.text.toString().isEmpty(), "Username is empty"
            )

            score += validateEditTextData(
                txtPassword, txtPassword.text.toString().isEmpty(), "Password is empty"
            )

            score += validateEditTextData(
                txtPhoneNumber, txtPhoneNumber.text.toString().isEmpty(), "Phone number is empty"
            )

            score += validateEditTextData(
                txtPhoneNumber, txtPhoneNumber.text.length != 11, "Phone number is invalid"
            )

            return score == 4
        }

        private fun isBankCardInformationValid(dialog: Dialog): Boolean {
            val txtCardName = dialog.findViewById<EditText>(R.id.txtCardName)
            val txtCardNumber = dialog.findViewById<EditText>(R.id.txtCardNumber)
            val txtCvv2 = dialog.findViewById<EditText>(R.id.txtCvv2)
            val txtMonth = dialog.findViewById<EditText>(R.id.txtMonth)
            val txtYear = dialog.findViewById<EditText>(R.id.txtYear)
            val txtPassword = dialog.findViewById<EditText>(R.id.txtPassword)

            var score = 6

            score += validateEditTextData(
                txtCardName, txtCardName.text.toString().isEmpty(), "Card name is empty"
            )

            score += validateEditTextData(
                txtCardNumber, txtCardNumber.text.toString().isEmpty(), "Card number is empty"
            )

            score += validateEditTextData(
                txtCardNumber,
                txtCardNumber.text.toString().replace(" ", "").length != 16,
                "Card number is invalid"
            )

            score += validateEditTextData(
                txtCvv2, txtCvv2.text.isEmpty(), "Cvv2 is empty"
            )

            score += validateEditTextData(
                txtCvv2, txtCvv2.text.length != 4, "Cvv2 is invalid"
            )

            score += validateEditTextData(
                txtMonth, txtMonth.text.isEmpty(), "Month is empty"
            )

            score += validateEditTextData(
                txtMonth, txtMonth.text.length != 2, "Month is invalid"
            )

            score += validateEditTextData(
                txtYear, txtYear.text.isEmpty(), "Year is empty"
            )

            score += validateEditTextData(
                txtYear, txtYear.text.length != 2, "Year is invalid"
            )

            score += validateEditTextData(
                txtPassword, txtPassword.text.isEmpty(), "Password is empty"
            )

            return score == 6
        }

        private fun isContactInformationValid(dialog: Dialog): Boolean {
            val txtName = dialog.findViewById<EditText>(R.id.txtName)
            val txtPhoneNumber = dialog.findViewById<EditText>(R.id.txtPhoneNumber)

            var score = 2

            score += validateEditTextData(
                txtName, txtName.text.toString().isEmpty(), "Name is empty"
            )

            score += validateEditTextData(
                txtPhoneNumber, txtPhoneNumber.text.toString().isEmpty(), "Phone number is empty"
            )

            score += validateEditTextData(
                txtPhoneNumber, txtPhoneNumber.text.length != 11, "Phone number is invalid"
            )

            return score == 2
        }

        private fun isNoteInformationValid(dialog: Dialog): Boolean {
            val txtName = dialog.findViewById<EditText>(R.id.txtName)
            return txtName.text.toString().isNotEmpty()
        }

        suspend fun registerPasscode(context: Context): Boolean =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_register_passcode)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                dialog.findViewById<Button>(R.id.btnRegister).setOnClickListener {
                    /* validate the passcode */
                    val txtPasscode = dialog.findViewById<EditText>(R.id.txtPasscode)

                    if (txtPasscode.text.isEmpty()) {
                        txtPasscode.error = "Passcode is empty."
                    } else {
                        txtPasscode.error = null

                        /* update the preferences */
                        val sharedPreferences =
                            context.getSharedPreferences(preferencesName, MODE_PRIVATE)

                        sharedPreferences.edit {
                            putBoolean("Registered", true)
                            putString("Passcode", txtPasscode.text.toString())
                        }

                        continuation.resume(true)
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }

        suspend fun ask(context: Context, icon: Int, title: String, message: String): Boolean =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_ask)
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

        suspend fun account(context: Context, account: Account? = null): Account? =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_account)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val txtName = dialog.findViewById<EditText>(R.id.txtName)
                val txtUsername = dialog.findViewById<EditText>(R.id.txtUsername)
                val txtPassword = dialog.findViewById<EditText>(R.id.txtPassword)
                val txtPhoneNumber = dialog.findViewById<EditText>(R.id.txtPhoneNumber)
                val txtComment = dialog.findViewById<EditText>(R.id.txtComment)

                val spinnerAccountType = dialog.findViewById<Spinner>(R.id.spinnerAccountType)
                val tvPhoneNumberLimiter = dialog.findViewById<TextView>(R.id.tvPhoneNumberLimiter)

                val btnApply = dialog.findViewById<Button>(R.id.btnApply)

                /* Setup Spinner */
                val adapter = ArrayAdapter(
                    context, R.layout.spinner_account_type, arrayOf(
                        "Social media", "Website", "Email address", "Others"
                    )
                )

                adapter.setDropDownViewResource(R.layout.spinner_account_type)
                spinnerAccountType.adapter = adapter/* Setup Spinner */

                setEditTextLimiter(txtPhoneNumber, tvPhoneNumberLimiter, 11, true)

                /* Loads information */
                if (account == null) {
                    btnApply.text = "Create account"
                } else {
                    txtName.setText(account.name)
                    txtUsername.setText(account.name)
                    txtPassword.setText(account.name)
                    txtPhoneNumber.setText(account.name)
                    txtComment.setText(account.name)
                }

                btnApply.setOnClickListener {
                    if (isAccountInformationValid(dialog)) {/* Create */
                        if (account == null) {
                            val account = Account(
                                name = txtName.text.toString(),
                                phoneNumber = txtPhoneNumber.text.toString(),
                                username = txtUsername.text.toString(),
                                password = txtPassword.text.toString(),
                                accountType = spinnerAccountType.selectedItemPosition,
                                comment = txtComment.text.toString()
                            )

                            continuation.resume(account)
                            dialog.dismiss()
                        } else {
                            account.name = txtName.text.toString()
                            account.phoneNumber = txtPhoneNumber.text.toString()
                            account.username = txtUsername.text.toString()
                            account.password = txtPassword.text.toString()
                            account.accountType = spinnerAccountType.selectedItemPosition
                            account.comment = txtComment.text.toString()

                            continuation.resume(account)
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()
            }

        suspend fun bankCard(context: Context, bankCard: BankCard? = null): BankCard? =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_bank_card)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val txtCardName = dialog.findViewById<EditText>(R.id.txtCardName)
                val txtCardNumber = dialog.findViewById<EditText>(R.id.txtCardNumber)
                val txtCvv2 = dialog.findViewById<EditText>(R.id.txtCvv2)
                val txtMonth = dialog.findViewById<EditText>(R.id.txtMonth)
                val txtYear = dialog.findViewById<EditText>(R.id.txtYear)
                val txtPassword = dialog.findViewById<EditText>(R.id.txtPassword)

                val tvCardNumberLimiter = dialog.findViewById<TextView>(R.id.tvCardNumberLimiter)
                val tvCvv2Limiter = dialog.findViewById<TextView>(R.id.tvCvv2Limiter)

                val btnApply = dialog.findViewById<Button>(R.id.btnApply)

                setEditTextLimiter(txtCardNumber, tvCardNumberLimiter, 16, true)
                setEditTextLimiter(txtCvv2, tvCvv2Limiter, 4)

                /* Loads information */
                if (bankCard == null) {
                    btnApply.text = "Create bank card"
                } else {
                    txtCardName.setText(bankCard.cardName)
                    txtCardNumber.setText(bankCard.cardNumber)
                    txtCvv2.setText(bankCard.cvv2)
                    txtMonth.setText(bankCard.month)
                    txtYear.setText(bankCard.year)
                    txtPassword.setText(bankCard.password)
                }

                btnApply.setOnClickListener {
                    if (isBankCardInformationValid(dialog)) {/* Create */
                        if (bankCard == null) {
                            val bankCard = BankCard(
                                cardName = txtCardName.text.toString(),
                                cardNumber = txtCardNumber.text.toString(),
                                cvv2 = txtCvv2.text.toString(),
                                month = txtMonth.text.toString(),
                                year = txtYear.text.toString(),
                                password = txtPassword.text.toString()
                            )

                            continuation.resume(bankCard)
                            dialog.dismiss()
                        } else {
                            bankCard.cardName = txtCardName.text.toString()
                            bankCard.cardNumber = txtCardNumber.text.toString()
                            bankCard.cvv2 = txtCvv2.text.toString()
                            bankCard.month = txtMonth.text.toString()
                            bankCard.year = txtYear.text.toString()
                            bankCard.password = txtPassword.text.toString()

                            continuation.resume(bankCard)
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()
            }

        suspend fun contact(context: Context, contact: Contact? = null): Contact? =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_contact)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val txtName = dialog.findViewById<EditText>(R.id.txtName)
                val txtPhoneNumber = dialog.findViewById<EditText>(R.id.txtPhoneNumber)
                val txtComment = dialog.findViewById<EditText>(R.id.txtComment)

                val tvPhoneNumberLimiter = dialog.findViewById<TextView>(R.id.tvPhoneNumberLimiter)
                val btnApply = dialog.findViewById<Button>(R.id.btnApply)

                setEditTextLimiter(txtPhoneNumber, tvPhoneNumberLimiter, 11, true)

                /* Loads information */
                if (contact == null) {
                    btnApply.text = "Create contact"
                } else {
                    txtName.setText(contact.name)
                    txtPhoneNumber.setText(contact.phoneNumber)
                    txtComment.setText(contact.comment)
                }

                btnApply.setOnClickListener {
                    if (isContactInformationValid(dialog)) {/* Create */
                        if (contact == null) {
                            val contact = Contact(
                                name = txtName.text.toString(),
                                phoneNumber = txtPhoneNumber.text.toString(),
                                comment = txtComment.text.toString()
                            )

                            continuation.resume(contact)
                            dialog.dismiss()
                        } else {
                            contact.name = txtName.text.toString()
                            contact.phoneNumber = txtPhoneNumber.text.toString()
                            contact.comment = txtComment.text.toString()

                            continuation.resume(contact)
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()
            }

        suspend fun note(context: Context, note: Note? = null): Note? =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_note)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val txtName = dialog.findViewById<EditText>(R.id.txtName)
                val txtContent = dialog.findViewById<EditText>(R.id.txtContent)

                val btnApply = dialog.findViewById<Button>(R.id.btnApply)

                /* Loads information */
                if (note == null) {
                    btnApply.text = "Create note"
                } else {
                    txtName.setText(note.name)
                    txtContent.setText(note.content)
                }

                btnApply.setOnClickListener {
                    if (isNoteInformationValid(dialog)) {/* Create */
                        if (note == null) {
                            val note = Note(
                                name = txtName.text.toString(),
                                content = txtContent.text.toString(),
                                modifyDate = getDate()
                            )

                            continuation.resume(note)
                            dialog.dismiss()
                        } else {
                            note.name = txtName.text.toString()
                            note.content = txtContent.text.toString()

                            continuation.resume(note)
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()
            }

        suspend fun textInput(
            context: Context,
            title: String,
            message: String,
            hint: String,
            defaultText: String = "",
            isPassword: Boolean = false,
            isNumber: Boolean
        ): String = suspendCancellableCoroutine { continuation ->
            val dialog = createDialog(context, R.layout.dialog_text_input)
            startDialogAnimation(dialog.findViewById(R.id.main))

            val editText = dialog.findViewById<EditText>(R.id.txtInput)

            dialog.findViewById<TextView>(R.id.tvTitle).text = title
            dialog.findViewById<TextView>(R.id.tvMessage).text = message

            editText.hint = hint
            editText.setText(defaultText)

            if (isPassword) {
                if (isNumber) {
                    editText.inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
                } else {
                    editText.inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                }

                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                if (isNumber) {
                    editText.inputType = TYPE_CLASS_NUMBER
                } else {
                    editText.inputType = TYPE_CLASS_TEXT
                }
            }

            dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
                val text = editText.text.toString()
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

        suspend fun theme(context: Context, defaultValue: Int): Int =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_theme)
                dialog.setCancelable(true)

                startDialogAnimation(dialog.findViewById(R.id.main))

                val btnLight = dialog.findViewById<RadioButton>(R.id.btnLight)
                val btnDark = dialog.findViewById<RadioButton>(R.id.btnDark)
                val btnSystem = dialog.findViewById<RadioButton>(R.id.btnSystem)

                when (defaultValue) {
                    0 -> {
                        btnLight.isChecked = true
                    }

                    1 -> {
                        btnDark.isChecked = true
                    }

                    2 -> {
                        btnSystem.isChecked = true
                    }
                }

                btnLight.setOnClickListener {
                    continuation.resume(0)
                    dialog.dismiss()
                }

                btnDark.setOnClickListener {
                    continuation.resume(1)
                    dialog.dismiss()
                }

                btnSystem.setOnClickListener {
                    continuation.resume(2)
                    dialog.dismiss()
                }

                dialog.show()
            }

        suspend fun lockTimeout(context: Context, defaultValue: Int): Int =
            suspendCancellableCoroutine { continuation ->
                val dialog = createDialog(context, R.layout.dialog_lock_timeout)
                dialog.setCancelable(true)
                startDialogAnimation(dialog.findViewById(R.id.main))

                val btn5Second = dialog.findViewById<RadioButton>(R.id.btn5Second)
                val btn10Second = dialog.findViewById<RadioButton>(R.id.btn10Second)
                val btn15Second = dialog.findViewById<RadioButton>(R.id.btn15Second)
                val btn30Second = dialog.findViewById<RadioButton>(R.id.btn30Second)

                btn5Second.setOnClickListener {
                    continuation.resume(5)
                    dialog.dismiss()
                }

                btn10Second.setOnClickListener {
                    continuation.resume(10)
                    dialog.dismiss()
                }

                btn15Second.setOnClickListener {
                    continuation.resume(15)
                    dialog.dismiss()
                }

                btn30Second.setOnClickListener {
                    continuation.resume(30)
                    dialog.dismiss()
                }

                when (defaultValue) {
                    5 -> {
                        btn5Second.isChecked = true
                    }

                    10 -> {
                        btn10Second.isChecked = true
                    }

                    15 -> {
                        btn15Second.isChecked = true
                    }

                    30 -> {
                        btn30Second.isChecked = true
                    }
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

            val chkNumbers = dialog.findViewById<CheckBox>(R.id.chkNumbers)
            val chkSymbols = dialog.findViewById<CheckBox>(R.id.chkSymbols)
            val chkLowerCase = dialog.findViewById<CheckBox>(R.id.chkLowerCase)

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

        fun confirm(context: Context, icon: Int, title: String, message: String) {
            val dialog = createDialog(context, R.layout.dialog_confirm)
            startDialogAnimation(dialog.findViewById(R.id.main))

            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)

            tvTitle.text = title
            tvMessage.text = message

            dialog.findViewById<ImageView>(R.id.imgLogo).setImageDrawable(
                ContextCompat.getDrawable(context, icon)
            )

            dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun exception(context: Context, exception: Exception) {
            val message = "${exception.message}}"
            confirm(context, R.drawable.ic_error, "Error", message)
        }
    }
}