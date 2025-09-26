package org.zendev.keepergen.activity.login

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.ActivityLoginBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.isDeviceConnected
import org.zendev.keepergen.tools.preferencesName
import org.zendev.keepergen.viewmodel.ApiViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityLoginBinding
    private lateinit var viewModel: ApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()

        b.btnLogin.setOnClickListener(this)
        b.btnBack.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBack -> {
                finish()
            }

            R.id.btnLogin -> {
                login()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ApiViewModel::class.java]
    }

    private fun login() {
        val username = b.txtUsername.text.toString()
        val password = b.txtPassword.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (isDeviceConnected(this)) {
                val dialog = Dialogs.load(
                    this@LoginActivity,
                    "Sending request",
                    "Please wait until login is complete."
                )

                lifecycleScope.launch {
                    dialog.show()

                    val response = viewModel.getUser(username)
                    if (response.isSuccessful) {
                        val apiUser = response.body()

                        if (apiUser != null) {
                            if (apiUser.username == username && apiUser.password == password) {
                                val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)
                                pref.edit {
                                    putBoolean("LoggedIn", true)
                                    putString("ApiUsername", apiUser.username)
                                }

                                setResult(RESULT_OK)
                                finish()
                            } else {
//                                Dialogs.confirm(
//                                    this@LoginActivity,
//                                    R.drawable.ic_error,
//                                    "Login failed",
//                                    "The username or password is invalid."
//                                )
                            }
                        }
                    } else {
                        when (response.code()) {
                            404 -> {
//                                Dialogs.confirm(
//                                    this@LoginActivity,
//                                    R.drawable.ic_error,
//                                    "No user found",
//                                    "The username is invalid."
//                                )
                            }
                        }
                    }

                    dialog.dismiss()
                }
            }
        }
    }
}