package org.zendev.keepergen.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.databinding.BsdAccountDetailsBinding
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.selectedItems
import org.zendev.keepergen.tools.shareText
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogAccountDetails(private val context: Context, private var account: Account) :
    BottomSheetDialogFragment(), OnClickListener {

    private lateinit var b: BsdAccountDetailsBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = BsdAccountDetailsBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()
        loadAccountInformation()

        b.layCopy.setOnClickListener(this)
        b.layShare.setOnClickListener(this)
        b.layDelete.setOnClickListener(this)
        b.layEdit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layEdit -> {
                lifecycleScope.launch {
                    account = Dialogs.account(context, account)

                    databaseViewModel.updateAccount(account)
                    loadAccountInformation()
                }
            }

            R.id.layCopy -> {
                copyTextToClipboard(context, "Account details", account.toString())
            }

            R.id.layShare -> {
                shareText(context, "Account details", account.toString())
            }

            R.id.layDelete -> {
                lifecycleScope.launch {
                    if (Dialogs.ask(
                            context,
                            icon = R.drawable.ic_warning,
                            "Delete account",
                            "Are you sure you want to delete this account?"
                        )
                    ) {
                        databaseViewModel.deleteAccount(account)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadAccountInformation() {
        b.tvAccountName.text = account.name
        b.tvAccountType.text = account.accountType
        when (account.accountType) {
            "Social media" -> {
                b.imgIcon.setImageResource(R.drawable.ic_telegram)
            }

            "Website" -> {
                b.imgIcon.setImageResource(R.drawable.ic_chrome)
            }

            "Email address" -> {
                b.imgIcon.setImageResource(R.drawable.ic_google)
            }

            "Others" -> {
                b.imgIcon.setImageResource(R.drawable.ic_earth)
            }
        }

        b.tvPhoneNumber.text = account.phoneNumber
        b.tvUsername.text = account.username
        b.tvPassword.text = "*".repeat(account.password.length)
        b.tvComment.text = account.comment
    }
}