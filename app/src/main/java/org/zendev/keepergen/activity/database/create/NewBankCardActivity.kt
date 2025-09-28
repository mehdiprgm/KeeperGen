package org.zendev.keepergen.activity.database.create

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.databinding.ActivityNewBankCardBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.CreditCardNumberFormattingTextWatcher
import org.zendev.keepergen.tools.disableActivityScreenShot
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class NewBankCardActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {
    private lateinit var b: ActivityNewBankCardBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var pref: SharedPreferences

    private lateinit var cardName: String

    private var month = 1
    private var year = 2025
    private var dayOfMonth = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityNewBankCardBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        setupViewModel()
        setupFormValidator()
        setOnBackPressedListener()

        loadCardName()
        loadDate()
        loadPreferences()

        formatBankCardNumber()

        b.btnFinish.setOnClickListener(this)
        b.tvDateResult.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvDateResult -> {
                DatePickerDialog(
                    this,
                    this,
                    year,
                    month,
                    dayOfMonth
                ).show()
            }

            R.id.btnFinish -> {
                if (isFormValid()) {
                    val bankCard = BankCard(
                        cardName = cardName,
                        cardNumber = b.txtCardNumber.text.toString(),
                        cvv2 = b.txtCvv2.text.toString(),
                        month = month.toString(),
                        year = year.toString(),
                        password = b.txtPassword.text.toString()
                    )

                    databaseViewModel.addBankCard(bankCard)
                    finish()
                }
            }
        }
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth

        b.tvDateResult.text = "$year / $month"
    }

    private fun isFormValid(): Boolean {
        var score = 3

        score += validateEditTextData(
            b.txtLayCardNumber,
            b.txtCardNumber.text.toString().isEmpty(),
            "Card number is empty."
        )

        score += validateEditTextData(
            b.txtLayCardNumber,
            b.txtCardNumber.text.toString().replace(" ", "").length != 16,
            "Card number is not correct."
        )

        score += validateEditTextData(
            b.txtLayCvv2,
            b.txtCvv2.text.toString().isEmpty(),
            "Cvv2 is empty."
        )

        score += validateEditTextData(
            b.txtLayCvv2,
            b.txtCvv2.text.toString().length < 3,
            "Cvv2 is not correct."
        )

        score += validateEditTextData(
            b.txtLayPassword,
            b.txtPassword.text.toString().isEmpty(),
            "Password is empty."
        )

        return score == 3
    }

    private fun loadCardName() {
        cardName = intent.getStringExtra("CardName")!!
    }

    private fun loadDate() {
        val calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)

        b.tvDateResult.text = "$year / $month"
    }

    private fun loadPreferences() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        if (!pref.getBoolean("Screenshot", false)) {
            disableActivityScreenShot(this)
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun formatBankCardNumber() {
        val formatter = CreditCardNumberFormattingTextWatcher()
        b.txtCardNumber.addTextChangedListener(formatter)
    }

    private fun setupFormValidator() {
        b.txtCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayCardNumber.error = "Card number is empty"
                } else if (s.toString().length != 19) {
                    b.txtLayCardNumber.error = "Card number is not correct"
                } else {
                    b.txtLayCardNumber.isErrorEnabled = false
                    b.txtLayCardNumber.error = null
                }
            }
        })

        b.txtCvv2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayCvv2.error = "Cvv2 is empty"
                } else if (s.toString().length < 3) {
                    b.txtLayCvv2.error = "Cvv2 is not correct"
                } else {
                    b.txtLayCvv2.isErrorEnabled = false
                    b.txtLayCvv2.error = null
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
                                    this@NewBankCardActivity,
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
}