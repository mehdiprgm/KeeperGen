package org.zendev.keepergen.adapter.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.databinding.BankcardLayoutBinding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.selectedItems

class BankCardAdapter(private val context: Context) :
    RecyclerView.Adapter<BankCardAdapter.BankCardViewHolder>() {

    private var showCheckBoxes = false

    var bankCards = emptyList<BankCard>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener {
        fun onItemClick(checkBox: MaterialCheckBox, bankCard: BankCard)

        fun onItemLongClick(checkBox: MaterialCheckBox, bankCard: BankCard)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BankCardViewHolder {
        val binding =
            BankcardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BankCardViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BankCardViewHolder, position: Int
    ) {
        val bankCard = bankCards[position]
        val b = holder.binding

        val popInAnim = AnimationUtils.loadAnimation(context, R.anim.pop_in)
        popInAnim.duration = 100

        val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDownAnim.duration = 100
        b.cardBankCard.animation = slideDownAnim

        getAllViews(b.layBankCard, false).forEach {
            popInAnim.duration += 30
            it.animation = popInAnim
        }

        b.tvCardName.text = bankCard.cardName.replaceFirstChar { it.uppercase() }
        b.tvCardNumber.text = bankCard.cardNumber
        b.tvExpireDate.text = "${bankCard.year} / ${bankCard.month}"

        b.chkSelected.isVisible = showCheckBoxes
        b.chkSelected.isChecked = selectedItems.contains(bankCard)

        b.layBankCard.setOnClickListener {
            itemClickListener?.onItemClick(b.chkSelected, bankCard)
        }

        b.layBankCard.setOnLongClickListener {
            itemClickListener?.onItemLongClick(b.chkSelected, bankCard)
            true
        }
    }

    override fun getItemCount(): Int {
        return bankCards.size
    }

    class BankCardViewHolder(val binding: BankcardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setShowCheckboxes(show: Boolean) {
        showCheckBoxes = show
        notifyDataSetChanged()
    }
}