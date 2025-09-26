package org.zendev.keepergen.activity.database

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.databinding.ActivityNewBankCardBinding
import org.zendev.keepergen.tools.CreditCardNumberFormattingTextWatcher
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class NewBankCardActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {
    private lateinit var b: ActivityNewBankCardBinding
    private lateinit var databaseViewModel: DatabaseViewModel

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

        loadCardName()
        loadDate()
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
                        cardNumber = cardName,
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
                } else if (s.toString().replace(" ", "").length != 16) {
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
}