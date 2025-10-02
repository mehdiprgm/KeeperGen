package org.zendev.keepergen.adapter.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.databinding.AccountLayoutBinding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.resizeTextViewDrawable
import org.zendev.keepergen.tools.selectedItems

class AccountAdapter(private val context: Context) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private var showCheckBoxes = false

    var accounts = emptyList<Account>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener {
        fun onItemClick(checkBox: MaterialCheckBox, account: Account)

        fun onItemLongClick(checkBox: MaterialCheckBox, account: Account)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AccountViewHolder {
        val binding =
            AccountLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AccountViewHolder, position: Int
    ) {
        val account = accounts[position]
        val b = holder.binding

        val popInAnim = AnimationUtils.loadAnimation(context, R.anim.pop_in)
        popInAnim.duration = 100

        val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDownAnim.duration = 100
        b.cardAccount.animation = slideDownAnim

        getAllViews(b.layAccount, false).forEach {
            popInAnim.duration += 30
            it.animation = popInAnim
        }

        when (account.accountType) {
            "Social media" -> {
                resizeTextViewDrawable(context, b.tvAccountName, R.drawable.ic_telegram, 25)
            }

            "Website" -> {
                resizeTextViewDrawable(context, b.tvAccountName, R.drawable.ic_chrome, 25)
            }

            "Email address" -> {
                resizeTextViewDrawable(context, b.tvAccountName, R.drawable.ic_google, 25)
            }

            "Others" -> {
                resizeTextViewDrawable(context, b.tvAccountName, R.drawable.ic_earth, 25)
            }
        }

        b.tvAccountName.text = account.name.replaceFirstChar { it.uppercase() }
        b.tvUsername.text = account.username
        b.tvPhoneNumber.text = account.phoneNumber

        b.chkSelected.isVisible = showCheckBoxes
        b.chkSelected.isChecked = selectedItems.contains(account)

        b.layAccount.setOnClickListener {
            itemClickListener?.onItemClick(b.chkSelected, account)
        }

        b.layAccount.setOnLongClickListener {
            itemClickListener?.onItemLongClick(b.chkSelected, account)
            true
        }
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    class AccountViewHolder(val binding: AccountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setShowCheckboxes(show: Boolean) {
        showCheckBoxes = show
        notifyDataSetChanged()
    }
}