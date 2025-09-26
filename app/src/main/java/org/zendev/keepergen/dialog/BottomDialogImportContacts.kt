package org.zendev.keepergen.dialog

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.BsdImportContactsBinding
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class BottomDialogImportContacts(private val context: Context) : BottomSheetDialogFragment(),
    OnClickListener {

    private lateinit var b: BsdImportContactsBinding
    private lateinit var databaseViewModel: DatabaseViewModel

    private val permissionRequestCode = 1

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
        b = BsdImportContactsBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        setupViewModel()

        b.layCamera.setOnClickListener(this)
        b.layGallery.setOnClickListener(this)
        b.layDevice.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layCamera -> {
                //Camera
            }

            R.id.layGallery -> {
                //Gallery
            }

            R.id.layDevice -> {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    importContacts()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        permissionRequestCode
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            val permissionDenied = grantResults.filter { it == PackageManager.PERMISSION_DENIED }

            if (permissionDenied.isEmpty()) {
                importContacts()
            } else {
                Dialogs.confirm(
                    context,
                    title = "Permission denied",
                    message = "The application needs your permission to access device contacts and it seems you denied it.",
                    DialogType.Warning
                )
            }
        }
    }

    private fun setupViewModel() {
        databaseViewModel = ViewModelProvider(this)[DatabaseViewModel::class.java]
    }

    private fun getDeviceContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ), null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)

                contactsList.add(
                    Contact(name = name, phoneNumber = number, comment = "No comment")
                )
            }
        }

        return contactsList
    }

    private fun importContacts() {
        lifecycleScope.launch {
            if (Dialogs.ask(
                    context,
                    R.drawable.ic_warning,
                    "Import contacts",
                    "Are you sure you want to start the operation?\n\nIf database contains contacts with same name they won't be added."
                )
            ) {
                val contacts = getDeviceContacts()

                for (contact in contacts) {
                    val currentContact = databaseViewModel.getContact(contact.name)

                    if (currentContact == null) {
                        databaseViewModel.addContact(contact)
                    } else {
                        databaseViewModel.updateContact(contact)
                    }
                }
            }
        }
    }
}