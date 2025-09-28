package org.zendev.keepergen.activity.database.create

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.databinding.ActivityNewAccountBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.disableActivityScreenShot
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class NewAccountActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityNewAccountBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var pref: SharedPreferences

    private lateinit var accountName: String
    private lateinit var accountType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityNewAccountBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        setOnBackPressedListener()
        setupViewModel()
        setupFormValidator()
        setupAutoCompleteDropDownMenu()

        loadAccountName()
        loadPreferences()

        b.btnFinish.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFinish -> {
                if (isFormValid()) {
                    val account = Account(
                        name = accountName,
                        username = b.txtUsername.text.toString(),
                        password = b.txtPassword.text.toString(),
                        phoneNumber = b.txtPhoneNumber.text.toString(),
                        comment = b.txtComment.text.toString(),
                        accountType = accountType
                    )

                    databaseViewModel.addAccount(account)
                    finish()
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun setupAutoCompleteDropDownMenu() {
        val items = listOf("Social media", "Website", "Email address", "Others")
        val adapter = ArrayAdapter(this, R.layout.drop_down_account_type_layout, items)

        b.actvAccountType.setAdapter(adapter)
        b.actvAccountType.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                accountType = adapterView.getItemAtPosition(i).toString()
            }
    }

    private fun setupFormValidator() {
        b.txtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayUsername.error = "Username is empty"
                } else {
                    b.txtLayUsername.isErrorEnabled = false
                    b.txtLayUsername.error = null
                }
            }
        })

        b.txtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayPassword.error = "Password is empty"
                } else {
                    b.txtLayPassword.isErrorEnabled = false
                    b.txtLayPassword.error = null
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
                                    this@NewAccountActivity,
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

    private fun isFormValid(): Boolean {
        var score = 3

        score += validateEditTextData(
            b.txtLayUsername,
            b.txtUsername.text.toString().isEmpty(),
            "Username is empty."
        )

        score += validateEditTextData(
            b.txtLayPassword,
            b.txtPassword.text.toString().isEmpty(),
            "Password is empty."
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

        return score == 3
    }

    private fun loadAccountName() {
        accountName = intent.getStringExtra("Name")!!
    }

    private fun loadPreferences() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        if (!pref.getBoolean("Screenshot", false)) {
            disableActivityScreenShot(this)
        }
    }

}