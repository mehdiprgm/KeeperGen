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
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.databinding.ActivityUpdateNoteBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.disableActivityScreenShot
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.tools.validateEditTextData
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class UpdateNoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityUpdateNoteBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var pref: SharedPreferences
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        setOnBackPressedListener()
        setupViewModel()
        setupFormValidator()

        loadNoteInformation()
        loadPreferences()

        b.btnFinish.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFinish -> {
                if (isFormValid()) {
                    note.name = b.txtName.text.toString()
                    note.content = b.txtContent.text.toString()

                    databaseViewModel.updateNote(note)
                    finish()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        var score = 1

        score += validateEditTextData(
            b.txtLayName,
            b.txtName.text.toString().isEmpty(),
            "Name is empty."
        )

        return score == 1
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
                                    this@UpdateNoteActivity,
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

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun setupFormValidator() {
        b.txtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val databaseNote = databaseViewModel.getNote(s.toString())

                if (s.toString().isEmpty()) {
                    b.txtLayName.error = "Name is empty"
                } else {
                    if (databaseNote == null) {
                        b.txtLayName.isErrorEnabled = false
                        b.txtLayName.error = null

                        b.btnFinish.isEnabled = true
                    } else {
                        if (databaseNote.name == note.name) {
                            b.txtLayName.isErrorEnabled = false
                            b.txtLayName.error = null

                            b.btnFinish.isEnabled = true
                        } else {
                            b.txtLayName.error = "Note already exists, select new name"
                            b.btnFinish.isEnabled = false
                        }
                    }
                }
            }
        })
    }

    private fun loadNoteInformation() {
        note = intent.getParcelableExtra("Note")!!

        b.txtName.setText(note.name)
        b.txtContent.setText(note.content)
    }

    private fun loadPreferences() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        if (!pref.getBoolean("Screenshot", false)) {
            disableActivityScreenShot(this)
        }
    }
}