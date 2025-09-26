package org.zendev.keepergen.activity.database

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.databinding.ActivityNewNoteBinding
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getDate
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class NewNoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityNewNoteBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityNewNoteBinding.inflate(layoutInflater)
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
                    val note = Note(
                        name = name,
                        content = b.txtContent.text.toString(),
                        modifyDate = getDate()
                    )

                    databaseViewModel.addNote(note)
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
            b.txtLayContent,
            b.txtContent.text.toString().isEmpty(),
            "Type something, can't be empty"
        )

        return score == 1
    }

    private fun setupFormValidator() {
        b.txtContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    b.txtLayContent.error = "Type something, can't be empty"
                    b.txtLayContent.isCounterEnabled = true
                } else {
                    b.txtLayContent.isErrorEnabled = false
                    b.txtLayContent.error = null
                }
            }
        })
    }
}