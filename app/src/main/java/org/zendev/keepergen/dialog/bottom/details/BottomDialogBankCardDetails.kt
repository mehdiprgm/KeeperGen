package org.zendev.keepergen.dialog.bottom.details

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import org.zendev.keepergen.activity.database.update.UpdateAccountActivity
import org.zendev.keepergen.activity.database.update.UpdateBankCardActivity
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.databinding.BsdBankcardDetailsBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.shareText
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogBankCardDetails(private val context: Context, private val bankCard: BankCard) :
    BottomSheetDialogFragment(), OnClickListener {

    private lateinit var b: BsdBankcardDetailsBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = BsdBankcardDetailsBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()
        loadBankCardInformation()

        b.layCopy.setOnClickListener(this)
        b.layEdit.setOnClickListener(this)
        b.layShare.setOnClickListener(this)
        b.layDelete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layEdit -> {
                val intent = Intent(context, UpdateBankCardActivity::class.java)
                intent.putExtra("BankCard", bankCard)

                startActivity(intent)
                dismiss()
            }

            R.id.layCopy -> {
                copyTextToClipboard(context, "Bank Card details", bankCard.toString())
            }

            R.id.layShare -> {
                shareText(context, "Bank Card details", bankCard.toString())
            }

            R.id.layDelete -> {
                lifecycleScope.launch {
                    if (Dialogs.Companion.ask(
                            context,
                            icon = R.drawable.ic_warning,
                            "Delete bank card",
                            "Are you sure you want to delete this bank card?"
                        )
                    ) {
                        databaseViewModel.deleteBankCard(bankCard)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadBankCardInformation() {
        b.tvCardName.text = bankCard.cardName
        b.tvCardNumber.text = bankCard.cardNumber
        b.tvDate.text = "${bankCard.year} / ${bankCard.month}"
        b.tvPassword.text = "*".repeat(bankCard.password.length)
    }
}