package org.zendev.keepergen.activity.database.update

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.ActivityUpdateContactBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.disableActivityScreenShot
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class UpdateContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityUpdateContactBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var pref: SharedPreferences
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityUpdateContactBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        setOnBackPressedListener()
        setupViewModel()
        setupFormValidator()

        loadContactInformation()
        loadPreferences()

        b.btnFinish.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFinish -> {
                if (isFormValid()) {
                    contact.name = b.txtName.text.toString()
                    contact.phoneNumber = b.txtPhoneNumber.text.toString()
                    contact.comment = b.txtComment.text.toString()

                    databaseViewModel.updateContact(contact)
                    finish()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        var score = 2

        score += validateEditTextData(
            b.txtLayName,
            b.txtName.text.toString().isEmpty(),
            "Name is empty."
        )

        score += validateEditTextData(
            b.txtLayPhoneNumber,
            b.txtPhoneNumber.text.toString().isEmpty(),
            "Phone number is empty."
        )


        score += validateEditTextData(
            b.txtLayPhoneNumber,
            b.txtPhoneNumber.text.toString().length != 11,
            "Phone number is not correct."
        )

        return score == 2
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun setOnBackPressedListener() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pref.getBoolean("ConfirmChanges", false)) {
                    var editTextsEmpty = true
                    val views = getAllViews(b.layNewAccount, false)

                    for (view in views) {
                        if (view is TextInputEditText) {
                            if (view.text.toString().isNotEmpty()) {
                                editTextsEmpty = false
                                break
                            }
                        }
                    }

                    /* if no text exists in the entire form */
                    if (editTextsEmpty) {
                        finish()
                    } else {
                        lifecycleScope.launch {
                            if (Dialogs.ask(
                                    this@UpdateContactActivity,
                                    R.drawable.ic_warning,
                                    "Discard changes",
                                    "Are you sure you want to exit?\nAll the information in this form will be list",
                                    false
                                )
                            ) {
                                finish()
                            }
                        }
                    }
                } else {
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(
            this, onBackPressedCallback
        )
    }

    private fun setupFormValidator() {
        b.txtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val databaseContact = databaseViewModel.getContact(s.toString())

                if (s.toString().isEmpty()) {
                    b.txtLayName.error = "Name is empty"
                } else {
                    if (databaseContact == null) {
                        b.txtLayName.isErrorEnabled = false
                        b.txtLayName.error = null

                        b.btnFinish.isEnabled = true
                    } else {
                        if (databaseContact.name == contact.name) {
                            b.txtLayName.isErrorEnabled = false
                            b.txtLayName.error = null

                            b.btnFinish.isEnabled = true
                        } else {
                            b.txtLayName.error = "Contact already exists, select new name"
                            b.btnFinish.isEnabled = false
                        }
                    }
                }
            }
        })

        b.txtPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayPhoneNumber.error = "Phone number is empty"
                    b.txtLayPhoneNumber.isCounterEnabled = true
                } else if (s.toString().length != 11) {
                    b.txtLayPhoneNumber.error = "Phone number is not correct"
                    b.txtLayPhoneNumber.isCounterEnabled = true
                } else {
                    b.txtLayPhoneNumber.isCounterEnabled = false
                    b.txtLayPhoneNumber.isErrorEnabled = false
                    b.txtLayPhoneNumber.error = null
                }
            }
        })
    }

    private fun loadContactInformation() {
        contact = intent.getParcelableExtra("Contact")!!

        b.txtName.setText(contact.name)
        b.txtPhoneNumber.setText(contact.phoneNumber)
        b.txtComment.setText(contact.comment)
    }

    private fun loadPreferences() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        if (!pref.getBoolean("Screenshot", false)) {
            disableActivityScreenShot(this)
        }
    }
}