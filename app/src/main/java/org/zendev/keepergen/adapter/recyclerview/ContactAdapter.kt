package org.zendev.keepergen.adapter.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.ContactLayoutBinding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.selectedItems

class ContactAdapter(private val context: Context) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var showCheckBoxes = false

    var contacts = emptyList<Contact>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener {
        fun onItemClick(checkBox: MaterialCheckBox, contact: Contact)

        fun onItemLongClick(checkBox: MaterialCheckBox, contact: Contact)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ContactViewHolder {
        val binding =
            ContactLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder, position: Int
    ) {
        val contact = contacts[position]
        val b = holder.binding

        val popInAnim = AnimationUtils.loadAnimation(context, R.anim.pop_in)
        popInAnim.duration = 100

        val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDownAnim.duration = 100
        b.cardContact.animation = slideDownAnim

        getAllViews(b.layContact, false).forEach {
            popInAnim.duration += 30
            it.animation = popInAnim
        }

        b.tvContactName.text = contact.name.replaceFirstChar { it.uppercase() }
        b.tvPhoneNumber.text = contact.phoneNumber
        b.tvComment.text = contact.comment

        b.tvCommentTitle.isVisible = contact.comment.isNotEmpty()
        b.tvComment.isVisible = contact.comment.isNotEmpty()

        b.chkSelected.isVisible = showCheckBoxes
        b.chkSelected.isChecked = selectedItems.contains(contact)

        b.layContact.setOnClickListener {
            itemClickListener?.onItemClick(b.chkSelected, contact)
        }

        b.layContact.setOnLongClickListener {
            itemClickListener?.onItemLongClick(b.chkSelected, contact)
            true
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(val binding: ContactLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setShowCheckboxes(show: Boolean) {
        showCheckBoxes = show
        notifyDataSetChanged()
    }
}