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
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.databinding.BsdNoteDetailsBinding
import org.zendev.keepergen.tools.copyTextToClipboard
import org.zendev.keepergen.tools.shareText
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogNoteDetails(private val context: Context, private val note: Note) :
    BottomSheetDialogFragment(), OnClickListener {

    private lateinit var b: BsdNoteDetailsBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.isDraggable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = BsdNoteDetailsBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()
        loadNoteInformation()

        b.layCopy.setOnClickListener(this)
        b.layShare.setOnClickListener(this)
        b.layDelete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layCopy -> {
                copyTextToClipboard(context, "Note details", note.toString())
            }

            R.id.layShare -> {
                shareText(context, "Note details", note.toString())
            }

            R.id.layDelete -> {
                lifecycleScope.launch {
                    if (Dialogs.ask(
                            context,
                            icon = R.drawable.ic_warning,
                            "Delete note",
                            "Are you sure you want to delete this note?"
                        )
                    ) {
                        databaseViewModel.deleteNote(note)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {/* used indexing instead of get method (get(ViewModel::class.java)) */
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun loadNoteInformation() {
        b.tvName.text = note.name
        b.tvContent.text = note.content
        b.tvModifyDate.text = note.modifyDate
    }
}