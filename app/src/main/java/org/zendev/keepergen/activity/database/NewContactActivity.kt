package org.zendev.keepergen.activity.database

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.ActivityNewContactBinding
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class NewContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityNewContactBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        setupViewModel()
        setupFormValidator()

        loadName()

        b.btnFinish.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFinish -> {
                if (isFormValid()) {
                    val contact = Contact(
                        name = name,
                        phoneNumber = b.txtPhoneNumber.text.toString(),
                        comment = b.txtComment.text.toString()
                    )

                    databaseViewModel.addContact(contact)
                    finish()
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadName() {
        name = intent.getStringExtra("Name")!!
    }

    private fun isFormValid(): Boolean {
        var score = 1

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

        return score == 1
    }

     private fun setupFormValidator() {
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
}