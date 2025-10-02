package org.zendev.keepergen.tools

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import org.zendev.keepergen.R
import org.zendev.keepergen.dialog.DialogType
import org.zendev.keepergen.dialog.Dialogs
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

/* Keepergen preferences */
const val preferencesName = "KGPREF"

val selectedItems = mutableSetOf<Any>()
val selectedViews = mutableSetOf<MaterialCheckBox>()

fun copyTextToClipboard(context: Context, label: String, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)

    clipboardManager.setPrimaryClip(clipData)
}

@SuppressLint("SourceLockedOrientationActivity")
fun lockActivityOrientation(activity: Activity) {
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun isDeviceSecure(context: Context): Boolean {
    val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure
}

fun shareText(context: Context, title: String, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, title)
    startActivity(context, shareIntent, null)
}

fun generateRandomColor(): Int {
//    val red = Random.nextInt(150, 256)  // Higher range for lighter shades
//    val green = Random.nextInt(150, 256)
//    val blue = Random.nextInt(150, 256)
//
//    return Color.rgb(red, green, blue)

    val lightShades = listOf(
        Color.rgb(173, 216, 230), // Light Blue
        Color.rgb(255, 255, 153), // Light Yellow
        Color.rgb(144, 238, 144), // Light Green
        Color.rgb(255, 200, 120), // Light Orange
        Color.rgb(210, 180, 140)  // Light Brown (Tan)
    )

    val baseColor = lightShades.random()

// Add a small brightness variation
    val offset = Random.nextInt(-15, 15)
    val red = (Color.red(baseColor) + offset).coerceIn(0, 255)
    val green = (Color.green(baseColor) + offset).coerceIn(0, 255)
    val blue = (Color.blue(baseColor) + offset).coerceIn(0, 255)

    return Color.rgb(red, green, blue)
}

fun generateRandomColors(count: Int): List<Int> {
    val randomColors = mutableSetOf<Int>()
    val themeColor = Color.rgb(208, 135, 162)

    while (randomColors.size < count) {
        val newColor = generateRandomColor()

        if (newColor != themeColor) {
            randomColors.add(newColor)
        }
    }

    return randomColors.toList()
}

fun getDate(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day)
}

fun setButtonBackground(context: Context, button: Button, colorResource: Int) {
    button.backgroundTintList = ContextCompat.getColorStateList(context, colorResource)
}

fun changeTheme(value: Int) {
    when (value) {
        0 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        1 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        2 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}

fun isDarkTheme(context: Context): Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

fun getAllViews(view: View, includeViewGroup: Boolean): MutableList<View> {
    val result = mutableListOf<View>()

    if (view is ViewGroup) {
        if (includeViewGroup) {
            result += view
        }

        for (i in 0 until view.childCount) {
            result += getAllViews(view.getChildAt(i), includeViewGroup)
        }
    } else {
        result += view
    }

    return result
}

fun startDialogAnimation(view: View) {
    var animationDuration = 100L
    val views = getAllViews(view, false)

    views.forEachIndexed { _, v ->
        animationDuration += 15

        val animator = ObjectAnimator.ofFloat(v, "translationY", 100f, 0f).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
        }

        animator.start()
    }
}

fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true // Added bluetooth check.
        else -> false
    }
}

fun isDeviceConnected(context: Context): Boolean {
    if (isInternetConnected(context)) {
        return true
    } else {
        Dialogs.confirm(
            context,
            title = "No internet connection",
            message = "This device is not connected to the internet.",
            dialogType = DialogType.Error
        )

        return false
    }
}

fun disableActivityScreenShot(activity: Activity) {
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
    )
}

fun resizeTextViewDrawable(context: Context, textView: TextView, drawableIcon: Int, size: Int) {
    val density = context.resources.displayMetrics.density
    val desiredWidthInPx = (size * density).toInt()
    val desiredHeightInPx = (size * density).toInt()

    val drawable = ContextCompat.getDrawable(context, drawableIcon)
    drawable?.setBounds(0, 0, desiredWidthInPx, desiredHeightInPx)
    textView.setCompoundDrawables(drawable, null, null, null)
}

fun generatePassword(
    length: Int, includeNumbers: Boolean, includeSymbols: Boolean, toLowerCase: Boolean
): String {
    val letters = ('A'..'Z')
    val numbers = ('0'..'9')
    val symbols = listOf('!', '@', '#', '$', '%', '^', '&', '*')

    val charPool = buildList {
        addAll(letters)
        if (includeNumbers) addAll(numbers)
        if (includeSymbols) addAll(symbols)
    }

    if (charPool.isEmpty()) {
        throw IllegalArgumentException("Character pool is empty.")
    }

    val password = (1..length).map { charPool.random() }.joinToString("")

    return if (toLowerCase) password.lowercase() else password
}

fun getPasswordStrength(password: String): Int {
    var score = 0

    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.length >= 16) score++

    if (password.any { it.isDigit() }) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return score
}

fun disableScreenPadding(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
        v.setPadding(0, 0, 0, 0)
        insets
    }
}

fun switchFragment(
    activity: AppCompatActivity, activeFragment: Fragment, newFragment: Fragment
): Fragment {
    if (newFragment == activeFragment) {
        return newFragment
    }

    val transaction = activity.supportFragmentManager.beginTransaction()
    // Hide the current active fragment
    transaction.hide(activeFragment)
    // Show the new fragment
    transaction.show(newFragment)
    // Commit the transaction
    transaction.commit()

    // Update the active fragment
    return newFragment
}