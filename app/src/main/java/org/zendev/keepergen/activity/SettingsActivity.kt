package org.zendev.keepergen.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.ActivitySettingsBinding
import org.zendev.keepergen.dialog.BottomDialogImportContacts
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.changeTheme
import org.zendev.keepergen.tools.disableScreenPadding
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class SettingsActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private lateinit var b: ActivitySettingsBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        loadSettings()

        b.switchConfirmChanges.setOnCheckedChangeListener(this)
        b.switchTakeScreenshot.setOnCheckedChangeListener(this)

        b.tvImportContacts.setOnClickListener(this)
        b.tvChangePasscode.setOnClickListener(this)
        b.tvReportBug.setOnClickListener(this)
        b.tvTitle.setOnClickListener(this)

        b.btnFollowSystem.setOnClickListener(this)
        b.btnDarkMode.setOnClickListener(this)
        b.btnLightMode.setOnClickListener(this)

        b.btn5Seconds.setOnClickListener(this)
        b.btn10Seconds.setOnClickListener(this)
        b.btn15Seconds.setOnClickListener(this)
        b.btn30Seconds.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvTitle -> {
                finish()
            }

            R.id.tvImportContacts -> {
                val importContactsDialog = BottomDialogImportContacts(this)
                importContactsDialog.show(supportFragmentManager, "Import Contacts")
            }

            R.id.tvChangePasscode -> {
                lifecycleScope.launch {
                    val passcode = Dialogs.textInput(
                        this@SettingsActivity,
                        "Change passcode",
                        "Please enter your new passcode",
                        "New passcode",
                        isPassword = true,
                        isNumber = true,
                        cancellable = true
                    )

                    if (passcode.isNotEmpty()) {
                        pref.edit {
                            putString("Passcode", passcode)
                        }
                    }
                }
            }

            R.id.btnDarkMode -> {
                pref.edit {
                    putInt("Theme", 1)
                    changeTheme(1)
                }
            }

            R.id.btnLightMode -> {
                pref.edit {
                    putInt("Theme", 0)
                    changeTheme(0)
                }
            }

            R.id.btnFollowSystem -> {
                pref.edit {
                    putInt("Theme", 2)
                    changeTheme(2)
                }
            }

            R.id.btn5Seconds -> {
                pref.edit {
                    putInt("LockTimeout", 5)
                }
            }

            R.id.btn10Seconds -> {
                pref.edit {
                    putInt("LockTimeout", 10)
                }
            }

            R.id.btn15Seconds -> {
                pref.edit {
                    putInt("LockTimeout", 15)
                }
            }

            R.id.btn30Seconds -> {
                pref.edit {
                    putInt("LockTimeout", 30)
                }
            }

            R.id.tvReportBug -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:mfcrisis2016@gmail.com".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "")
                    putExtra(Intent.EXTRA_TITLE, "Bug KEEPERGEN")
                }

                /* optional: restrict to Gmail app if installed */
                intent.setPackage("com.google.android.gm")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when (button?.id) {
            R.id.switchConfirmChanges -> {
                setSwitchText(button as SwitchMaterial)

                pref.edit {
                    putBoolean("ConfirmChanges", isChecked)
                }
            }

            R.id.switchTakeScreenshot -> {
                setSwitchText(button as SwitchMaterial)

                pref.edit {
                    putBoolean("Screenshot", isChecked)
                }
            }
        }
    }

    private fun loadSettings() {
        pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        /* Load theme settings */
        val theme = pref.getInt("Theme", 0)
        when (theme) {
            0 -> {
                b.btnLightMode.isChecked = true
            }

            1 -> {
                b.btnDarkMode.isChecked = true
            }

            2 -> {
                b.btnFollowSystem.isChecked = true
            }
        }

        /* Load lock timeout */
        val lockTimeout = pref.getInt("LockTimeout", 5)
        when (lockTimeout) {
            5 -> {
                b.btn5Seconds.isChecked = true
            }

            10 -> {
                b.btn10Seconds.isChecked = true
            }

            15 -> {
                b.btn15Seconds.isChecked = true
            }

            30 -> {
                b.btn30Seconds.isChecked = true
            }
        }

        b.switchConfirmChanges.isChecked = pref.getBoolean("ConfirmChanges", false)
        b.switchTakeScreenshot.isChecked = pref.getBoolean("Screenshot", false)

        setSwitchText(b.switchConfirmChanges)
        setSwitchText(b.switchTakeScreenshot)
    }

    private fun setSwitchText(switch: SwitchMaterial) {
        if (switch.isChecked) {
            switch.text = "Enabled"
        } else {
            switch.text = "Disabled"
        }
    }
}

//b.layDisplay.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)