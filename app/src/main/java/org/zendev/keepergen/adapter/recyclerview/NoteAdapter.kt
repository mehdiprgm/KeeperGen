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
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.databinding.NoteLayoutBinding
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.selectedItems

class NoteAdapter(private val context: Context) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var showCheckBoxes = false

    var notes = emptyList<Note>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener {
        fun onItemClick(checkBox: MaterialCheckBox, note: Note)

        fun onItemLongClick(checkBox: MaterialCheckBox, note: Note)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        val note = notes[position]
        val b = holder.binding

        val popInAnim = AnimationUtils.loadAnimation(context, R.anim.pop_in)
        popInAnim.duration = 100

        val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDownAnim.duration = 100
        b.cardNote.animation = slideDownAnim

        getAllViews(b.layNote, false).forEach {
            popInAnim.duration += 30
            it.animation = popInAnim
        }

        b.tvNoteName.text = note.name.replaceFirstChar { it.uppercase() }
        b.tvModifyDate.text = note.modifyDate

        b.chkSelected.isVisible = showCheckBoxes
        b.chkSelected.isChecked = selectedItems.contains(note)

        if (note.content.isEmpty()) {
            b.tvContent.text = "Empty"
        } else if (note.content.length > 30) {
            b.tvContent.text = note.content.substring(0, 27) + "..."
        } else {
            b.tvContent.text = note.content
        }

        b.layNote.setOnClickListener {
            itemClickListener?.onItemClick(b.chkSelected, note)
        }

        b.layNote.setOnLongClickListener {
            itemClickListener?.onItemLongClick(b.chkSelected, note)
            true
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }


    class NoteViewHolder(val binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    fun setShowCheckboxes(show: Boolean) {
        showCheckBoxes = show
        notifyDataSetChanged()
    }
}