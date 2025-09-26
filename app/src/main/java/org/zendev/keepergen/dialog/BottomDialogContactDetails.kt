package org.zendev.keepergen.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.BsdBankcardDetailsBinding
import org.zendev.keepergen.databinding.BsdContactDetailsBinding
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.shareText
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogContactDetails(private val context: Context, private val contact: Contact) :
    BottomSheetDialogFragment(), OnClickListener {

    private lateinit var b: BsdContactDetailsBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = BsdContactDetailsBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()
        loadContactsInformation()

        b.layCopy.setOnClickListener(this)
        b.layShare.setOnClickListener(this)
        b.layDelete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layCopy -> {
                copyTextToClipboard(context, "Contact details", contact.toString())
            }

            R.id.layShare -> {
                shareText(context, "Contact details", contact.toString())
            }

            R.id.layDelete -> {
                lifecycleScope.launch {
                    if (Dialogs.ask(
                            context,
                            icon = R.drawable.ic_warning,
                            "Delete contact",
                            "Are you sure you want to delete this contact?"
                        )
                    ) {
                        databaseViewModel.deleteContact(contact)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadContactsInformation() {
        b.tvName.text = contact.name
        b.tvPhoneNumber.text = contact.phoneNumber
        b.tvComment.text = contact.comment
    }
}