package org.zendev.keepergen.activity.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.activity.login.PasscodeLoginActivity
import org.zendev.keepergen.databinding.ActivityWelcomeBinding
import org.zendev.keepergen.dialog.Dialogs

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b : ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.btnCreatePasscode.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnCreatePasscode -> {
                lifecycleScope.launch {
                    if (Dialogs.registerPasscode(this@WelcomeActivity)) {
                        startActivity(Intent(this@WelcomeActivity, PasscodeLoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}