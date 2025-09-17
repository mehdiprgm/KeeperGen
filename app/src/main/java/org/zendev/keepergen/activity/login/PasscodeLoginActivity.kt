package org.zendev.keepergen.activity.login

import android.animation.ObjectAnimator
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.zendev.keepergen.R
import org.zendev.keepergen.activity.MainActivity
import org.zendev.keepergen.databinding.ActivityPasscodeLoginBinding
import org.zendev.keepergen.dialog.Dialogs
import org.zendev.keepergen.dialog.Dialogs.Companion.confirm
import org.zendev.keepergen.tools.getAllViews
import org.zendev.keepergen.tools.isDeviceSecure
import org.zendev.keepergen.tools.lockActivityOrientation
import org.zendev.keepergen.tools.preferencesName

class PasscodeLoginActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var b: ActivityPasscodeLoginBinding
    private val REQUEST_CODE = 1001

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityPasscodeLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.imgFingerprint.setOnClickListener(this)
        b.txtPasscode.addTextChangedListener(this)

        b.btn0.setOnClickListener(this)
        b.btn1.setOnClickListener(this)
        b.btn2.setOnClickListener(this)
        b.btn3.setOnClickListener(this)
        b.btn4.setOnClickListener(this)
        b.btn5.setOnClickListener(this)
        b.btn6.setOnClickListener(this)
        b.btn7.setOnClickListener(this)
        b.btn8.setOnClickListener(this)
        b.btn9.setOnClickListener(this)

        b.btnDelete.setOnClickListener(this)
        b.btnClear.setOnClickListener(this)

        lockActivityOrientation(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9 -> {
                /* get the textview text and append it to the edittext */
                b.txtPasscode.setText("${b.txtPasscode.text}${(view as TextView).text}")
            }

            R.id.btnDelete -> {
                val text = b.txtPasscode.text.toString()

                if (text.isNotEmpty()) {
                    /* remove the last character */
                    b.txtPasscode.setText(text.dropLast(1))

                    /* move cursor to the end */
                    b.txtPasscode.setSelection(b.txtPasscode.text.length)
                }
            }

            R.id.btnClear -> {
                b.txtPasscode.text.clear()
            }

            R.id.imgFingerprint -> {
                if (isDeviceSecure(this)) {
                    requestDevicePin()
                } else {
                    Dialogs.confirm(
                        this,
                        R.drawable.ic_microchip,
                        "Biometric is unavailable",
                        "This device doesn't have lock screen or registered fingerprint.\nChange it from device settings and try again."
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startKeyPadAnimation()
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        /* get the result */
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                b.txtPasscode.text.clear()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = b.txtPasscode.text.toString()

        if (text.isNotEmpty() && text.length == 4) {
            val databasePasscode =
                getSharedPreferences(preferencesName, MODE_PRIVATE).getString(
                    "Passcode", ""
                )

            if (text == databasePasscode) {
                /* clear the edittext and attempts and start the main activity */
                attempts = 0
                b.txtPasscode.setText("")

                startActivity(Intent(this@PasscodeLoginActivity, MainActivity::class.java))
            } else {
                attempts++
                b.txtPasscode.setText("")

                if (attempts == 3) {
                    attempts = 0

                    val pref = getSharedPreferences(preferencesName, MODE_PRIVATE)
                    val timeout = pref.getInt("LockTimeout", 5)
                    startCountdownTimer(timeout)
                } else {
                    b.tvMessage.text = "The passcode is not valid"

                    Handler(Looper.getMainLooper()).postDelayed({
                        b.tvMessage.text = "Enter Passcode"
                    }, 2000)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun requestDevicePin() {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val intent =
            keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Please enter your PIN")

        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun startCountdownTimer(seconds: Int) {
        isLoginOptionsEnabled(false)

        val timer =
            object : CountDownTimer(seconds.toLong() * 1000, 1000) { // 10 seconds, tick every 1 sec
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000

                    if (secondsRemaining == 0L) {
                        onFinish()
                    } else {
                        b.tvMessage.text =
                            "Too many attempts, try again in ${secondsRemaining} seconds"
                    }
                }

                override fun onFinish() {
                    b.tvMessage.text = "Enter Passcode"
                    isLoginOptionsEnabled(true)
                }
            }

        timer.start()
    }

    private fun isLoginOptionsEnabled(enabled: Boolean) {
        getAllViews(b.main, false).forEach { view ->
            view.isEnabled = enabled
        }

        /* this must be always disabled */
        b.txtPasscode.isEnabled = false
    }

    private fun startKeyPadAnimation() {
        var animationDuration = 500L
        val views = getAllViews(b.layPasscode,false)

        views.forEachIndexed { _, v ->
            animationDuration += 50

            val animator = ObjectAnimator.ofFloat(v, "rotationY", 100f, 0f).apply {
                duration = animationDuration
                interpolator = AccelerateDecelerateInterpolator()
            }

            animator.start()
        }
    }
}