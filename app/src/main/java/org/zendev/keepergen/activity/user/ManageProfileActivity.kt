package org.zendev.keepergen.activity.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.api.model.User
import org.zendev.keepergen.databinding.ActivityManageProfileBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.tools.preferencesName
import androidx.core.content.edit

class ManageProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityManageProfileBinding
    private lateinit var user: User

    private val contentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadUserInformation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityManageProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.btnBack.setOnClickListener(this)
        b.tvLogout.setOnClickListener(this)
        b.tvEditProfile.setOnClickListener(this)

        loadUserInformation()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBack -> {
                finish()
            }

            R.id.tvLogout -> {
                lifecycleScope.launch {
                    if (Dialogs.ask(
                            this@ManageProfileActivity,
                            R.drawable.ic_logout,
                            "Logout",
                            "Are you sure you want to logut?"
                        )
                    ) {
                        val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)

                        pref.edit {
                            putBoolean("LoggedIn", false)
                            remove("ApiUsername")
                        }

                        finish()
                    }
                }
            }

            R.id.tvEditProfile -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("user", user)

                contentLauncher.launch(intent)
            }
        }
    }

    private fun loadUserInformation() {
        user = intent.getSerializableExtra("user") as User

        b.tvUsername.text = user.username
        b.tvPhoneNumber.text = user.phoneNumber
        b.tvCreateDate.text = user.createDate

        if (user.isLocked) {
            b.tvProfileStatusTitle.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_multiply_smaller,
                0
            )
        } else {
            b.tvProfileStatusTitle.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_check2_smaller,
                0
            )
        }
    }
}