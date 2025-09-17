package org.zendev.keepergen.activity.screens

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.zendev.keepergen.R
import org.zendev.keepergen.activity.login.PasscodeLoginActivity
import org.zendev.keepergen.databinding.ActivitySplashScreenBinding
import org.zendev.keepergen.tools.changeTheme
import org.zendev.keepergen.tools.lockActivityOrientation
import org.zendev.keepergen.tools.preferencesName


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var b: ActivitySplashScreenBinding
    private val delay = 800L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lockActivityOrientation(this)
        loadSettings()
    }

    override fun onStart() {
        super.onStart()
        loadStartAnimation()
    }

    private fun loadStartAnimation() {
        val rotate180Animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        rotate180Animation.duration = delay
        fadeInAnimation.duration = delay

        b.imgLogo.animation = rotate180Animation
        b.tvAppName.animation = fadeInAnimation

        lifecycleScope.launch {
            delay(delay)

            if (isUserRegistered()) {
                startActivity(Intent(this@SplashScreenActivity, PasscodeLoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
            }

            finish()
        }
    }

    private fun isUserRegistered(): Boolean {
        /* check if user registered and created the local passcode */
        return getSharedPreferences(preferencesName, MODE_PRIVATE).getBoolean(
            "Registered",
            false
        )
    }

    private fun loadSettings() {
        val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)
        changeTheme(pref.getInt("Theme", 2))
    }
}