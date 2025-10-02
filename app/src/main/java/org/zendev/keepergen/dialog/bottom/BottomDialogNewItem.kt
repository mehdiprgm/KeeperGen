package org.zendev.keepergen.dialog.bottom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.activity.database.create.NewAccountActivity
import org.zendev.keepergen.activity.database.create.NewBankCardActivity
import org.zendev.keepergen.activity.database.create.NewContactActivity
import org.zendev.keepergen.activity.database.create.NewNoteActivity
import org.zendev.keepergen.databinding.BsdNewItemBinding
import org.zendev.keepergen.dialog.DialogType
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogNewItem(private val context: Context) : BottomSheetDialogFragment(),
    View.OnClickListener {
    private lateinit var b: BsdNewItemBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = BsdNewItemBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()

        b.layAccount.setOnClickListener(this)
        b.layBankCard.setOnClickListener(this)
        b.layContact.setOnClickListener(this)
        b.layNote.setOnClickListener(this)
        b.layPassword.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layAccount -> {
                lifecycleScope.launch {
                    val accountName = Dialogs.Companion.textInput(
                        context,
                        title = "Account name",
                        message = "Please enter your account name",
                        hint = "New Account",
                        defaultText = "",
                        isPassword = false,
                        isNumber = false,
                        cancellable = true
                    )

                    if (accountName.isNotEmpty()) {
                        val account = databaseViewModel.getAccount(accountName)
                        if (account == null) {
                            val intent = Intent(context, NewAccountActivity::class.java)
                            intent.putExtra("Name", accountName)

                            startActivity(intent)
                            dismiss()
                        } else {
                            Dialogs.Companion.confirm(
                                context,
                                title = "Account exists",
                                message = "Select other name for this account",
                                DialogType.Error
                            )
                        }
                    }
                }
            }

            R.id.layBankCard -> {
                lifecycleScope.launch {
                    val bankCardName = Dialogs.Companion.textInput(
                        context,
                        title = "Card name",
                        message = "Please enter your card name",
                        hint = "New card",
                        defaultText = "",
                        isPassword = false,
                        isNumber = false,
                        cancellable = true
                    )

                    if (bankCardName.isNotEmpty()) {
                        val bankCard = databaseViewModel.getBankCard(bankCardName)
                        if (bankCard == null) {
                            val intent = Intent(context, NewBankCardActivity::class.java)
                            intent.putExtra("CardName", bankCardName)

                            startActivity(intent)
                            dismiss()
                        } else {
                            Dialogs.Companion.confirm(
                                context,
                                title = "Bank Card exists",
                                message = "Select other name for this card",
                                DialogType.Error
                            )
                        }
                    }
                }
            }

            R.id.layContact -> {
                lifecycleScope.launch {
                    val contactName = Dialogs.Companion.textInput(
                        context,
                        title = "Name",
                        message = "Please enter your contact name",
                        hint = "New contact",
                        defaultText = "",
                        isPassword = false,
                        isNumber = false,
                        cancellable = true
                    )

                    if (contactName.isNotEmpty()) {
                        val contact = databaseViewModel.getContact(contactName)
                        if (contact == null) {
                            val intent = Intent(context, NewContactActivity::class.java)
                            intent.putExtra("Name", contactName)

                            startActivity(intent)
                            dismiss()
                        } else {
                            Dialogs.Companion.confirm(
                                context,
                                title = "Contact exists",
                                message = "Select other name for this contact",
                                DialogType.Error
                            )
                        }
                    }
                }
            }

            R.id.layNote -> {
                lifecycleScope.launch {
                    val noteName = Dialogs.Companion.textInput(
                        context,
                        title = "Name",
                        message = "Please enter your note name",
                        hint = "New note",
                        defaultText = "",
                        isPassword = false,
                        isNumber = false,
                        cancellable = true
                    )

                    if (noteName.isNotEmpty()) {
                        val note = databaseViewModel.getNote(noteName)
                        if (note == null) {
                            val intent = Intent(context, NewNoteActivity::class.java)
                            intent.putExtra("Name", noteName)

                            startActivity(intent)
                            dismiss()
                        } else {
                            Dialogs.Companion.confirm(
                                context,
                                title = "Note exists",
                                message = "Select other name for this note",
                                DialogType.Error
                            )
                        }
                    }
                }
            }

            R.id.layPassword -> {
                Dialogs.Companion.newPassword(context)
                dismiss()
            }
        }
    }

    private fun setupViewModel() {
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }
}