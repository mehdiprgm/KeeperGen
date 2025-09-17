package org.zendev.keepergen.dialog

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
import org.zendev.keepergen.databinding.BsdNewItemBinding
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    val newAccount = Dialogs.account(context, null)
                    if (newAccount != null) {
                        databaseViewModel.addAccount(newAccount)
                    }

                    dismiss()
                }
            }

            R.id.layBankCard -> {
                lifecycleScope.launch {
                    val newBankCard = Dialogs.bankCard(context, null)
                    if (newBankCard != null) {
                        databaseViewModel.addBankCard(newBankCard)
                    }

                    dismiss()
                }
            }

            R.id.layContact -> {
                lifecycleScope.launch {
                    val newContact = Dialogs.contact(context, null)
                    if (newContact != null) {
                        databaseViewModel.addContact(newContact)
                    }

                    dismiss()
                }
            }

            R.id.layNote -> {
                lifecycleScope.launch {
                    val newNote = Dialogs.note(context, null)
                    if (newNote != null) {
                        databaseViewModel.addNote(newNote)
                    }

                    dismiss()
                }
            }

            R.id.layPassword -> {
                Dialogs.newPassword(context)
                dismiss()
            }
        }
    }

    private fun setupViewModel() {
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }
}