package org.zendev.keepergen.activity.user

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.api.model.User
import org.zendev.keepergen.databinding.ActivityEditProfileBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.viewmodel.ApiViewModel

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityEditProfileBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadInformation()

        b.btnUpdate.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnUpdate -> {
                updateUser()

                val viewModel = ViewModelProvider(this)[ApiViewModel::class.java]
                val loadDialog = Dialogs.load(
                    this, "Sending request", "Updating user information, please wait..."
                )

                loadDialog.show()

                CoroutineScope(Dispatchers.IO).launch {
                    val response = viewModel.updateUser(user)

                    if (response.isSuccessful) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Dialogs.confirm(
                                this@EditProfileActivity,
                                R.drawable.ic_error,
                                "Updating information failed",
                                "Failed to update the user."
                            )
                        }
                    }

                    loadDialog.dismiss()
                }
            }
        }
    }

    private fun loadInformation() {
        user = intent.getSerializableExtra("user") as User

        b.txtUsername.setText(user.username)
        b.txtPassword.setText(user.password)
        b.txtSecurityCode.setText(user.securityCode)
        b.txtPhoneNumber.setText(user.phoneNumber)
    }

    private fun updateUser() {
        val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

        user.username = b.txtUsername.text.toString()
        user.password = b.txtPassword.text.toString()
        user.securityCode = b.txtSecurityCode.text.toString()
        user.phoneNumber = b.txtPhoneNumber.text.toString()

        pref.edit {
            putString("ApiUsername", user.username)
        }
    }

}