package org.zendev.keepergen.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.databinding.ActivitySettingsBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.changeTheme
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class SettingsActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private lateinit var b: ActivitySettingsBinding
    private lateinit var pref: SharedPreferences
    private lateinit var databaseViewModel: DatabaseViewModel

    private var permissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
        loadSettings()

        b.btnBack.setOnClickListener(this)

        b.layBehaviour.setOnClickListener(this)
        b.layDisplay.setOnClickListener(this)
        b.layLockTimeout.setOnClickListener(this)
        b.layScreenshot.setOnClickListener(this)
        b.layChangePasscode.setOnClickListener(this)
        b.layReportBug.setOnClickListener(this)
        b.layDevice.setOnClickListener(this)

        b.switchConfirmChanges.setOnCheckedChangeListener(this)
        b.switchScreenShot.setOnCheckedChangeListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBack -> {
                finish()
            }

            R.id.layBehaviour -> {
                b.switchConfirmChanges.isChecked = !b.switchConfirmChanges.isChecked
            }

            R.id.layDisplay -> {
                lifecycleScope.launch {
                    val theme = pref.getInt("Theme", 2)
                    val result = Dialogs.theme(this@SettingsActivity, theme)

                    pref.edit {
                        putInt("Theme", result)
                        changeTheme(result)
                    }
                }
            }

            R.id.layLockTimeout -> {
                lifecycleScope.launch {
                    val timeout = pref.getInt("LockTimeout", 5)
                    val newTimeout = Dialogs.lockTimeout(this@SettingsActivity, timeout)

                    pref.edit {
                        putInt("LockTimeout", newTimeout)
                    }
                }
            }

            R.id.layScreenshot -> {
                b.switchScreenShot.isChecked = !b.switchScreenShot.isChecked
            }

            R.id.layChangePasscode -> {
                lifecycleScope.launch {
                    val passcode = Dialogs.textInput(
                        this@SettingsActivity,
                        "Change passcode",
                        "Please enter your new passcode",
                        "New passcode",
                        isPassword = true,
                        isNumber = true
                    )

                    if (passcode.isNotEmpty()) {
                        pref.edit {
                            putString("Passcode", passcode)
                        }
                    }
                }
            }

            R.id.layReportBug -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
//                    Intent.setData = "mailto:mfcrisis2016@gmail.com".toUri()
//                    putExtra(Intent.EXTRA_SUBJECT, "")
//                    putExtra(Intent.EXTRA_TITLE, "Bug KEEPERGEN")
                }

                /* optional: restrict to Gmail app if installed */
                intent.setPackage("com.google.android.gm")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            R.id.layDevice -> {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    lifecycleScope.launch {
                        if (Dialogs.ask(
                                this@SettingsActivity,
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
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_CONTACTS
                        )
                    ) {
                        Dialogs.confirm(
                            this,
                            R.drawable.ic_error,
                            "Permission denied",
                            "The application needs your permission to access device contacts and it seems you denied it.\n\nYou have to enable the permissions manually by going into the application properties."
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_CONTACTS), permissionRequestCode
                        )
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when (button?.id) {
            R.id.switchConfirmChanges -> {
                pref.edit {
                    putBoolean("ConfirmChanges", isChecked)
                }
            }

            R.id.switchScreenShot -> {
                pref.edit {
                    putBoolean("Screenshot", isChecked)
                }
            }
        }
    }

    private fun setupViewModel() {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
    }

    private fun loadSettings() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        b.switchConfirmChanges.isChecked = pref.getBoolean("ConfirmChanges", false)
        b.switchScreenShot.isChecked = pref.getBoolean("Screenshot", false)
    }

    private fun getDeviceContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()

        val cursor = contentResolver.query(
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
}