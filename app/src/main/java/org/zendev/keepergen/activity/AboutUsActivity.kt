package org.zendev.keepergen.activity

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.ActivityAboutUsBinding
import org.zendev.keepergen.tools.disableScreenPadding

class AboutUsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var b: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(b.root)
        disableScreenPadding(b.root)

        rotateApplicationLogo()
        enableLayoutTransitions()

        b.imgGmail.setOnClickListener(this)
        b.imgTelegram.setOnClickListener(this)
        b.imgInstagram.setOnClickListener(this)
        b.imgGithub.setOnClickListener(this)

        b.layAppInformation.setOnClickListener(this)
        b.layFeatures.setOnClickListener(this)
        b.layFixedBugs.setOnClickListener(this)

        b.tvTitle.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layAppInformation -> {
                setAppInformationCardViewVisibility(!b.viewSep1.isVisible)
            }

            R.id.layFeatures -> {
                setFeaturesCardViewVisibility(!b.tvFeature1.isVisible)
            }

            R.id.layFixedBugs -> {
                setFixedBugsCardViewVisibility(!b.tvFixedBug1.isVisible)
            }

            R.id.tvTitle -> {
                finish()
            }

            R.id.imgGmail -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:mfcrisis2016@gmail.com".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "")
                }

                /* optional: restrict to Gmail app if installed */
                intent.setPackage("com.google.android.gm")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            R.id.imgTelegram -> {
                val telegramIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://t.me/zenDEv2".toUri()/* optional: limit to Telegram app only */
                    setPackage("org.telegram.messenger")
                }

                if (telegramIntent.resolveActivity(packageManager) != null) {
                    startActivity(telegramIntent)
                } else {/* fallback: open in browser if Telegram is not installed */
                    val browserIntent = Intent(Intent.ACTION_VIEW, "https://t.me/zenDEv2".toUri())
                    startActivity(browserIntent)
                }
            }

            R.id.imgInstagram -> {
                val uri = "http://instagram.com/_u/mehdi.la.79".toUri()
                val instagramIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.instagram.android")
                }

                if (instagramIntent.resolveActivity(packageManager) != null) {
                    startActivity(instagramIntent)
                } else {/* fallback to browser if Instagram app isn't installed */
                    val webIntent = Intent(
                        Intent.ACTION_VIEW, "http://instagram.com/mehdi.la.79".toUri()
                    )
                    startActivity(webIntent)
                }
            }

            R.id.imgGithub -> {
                val intent = Intent(Intent.ACTION_VIEW, "https://github.com/mehdiprgm".toUri())
                startActivity(intent)
            }
        }
    }

    private fun rotateApplicationLogo() {
        val animator = ObjectAnimator.ofFloat(b.imgLogo, View.ROTATION, 0f, 360f)

        animator.duration = 5000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()

        animator.start()
    }

    private fun enableLayoutTransitions() {
        b.layAppInformation.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        b.layFeatures.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        b.layFixedBugs.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setAppInformationCardViewVisibility(visible: Boolean) {
        var animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse)
        if (!visible) {
            animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180)
        }

        b.imgClose1.animation = animation

        b.viewSep1.isVisible = visible
        b.tvAppDescription.isVisible = visible
    }

    private fun setFeaturesCardViewVisibility(visible: Boolean) {
        var animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse)
        if (!visible) {
            animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180)
        }

        b.imgClose2.animation = animation

        b.tvFeature1.isVisible = visible
        b.tvFeature2.isVisible = visible
        b.tvFeature3.isVisible = visible
        b.tvFeature4.isVisible = visible
        b.tvFeature5.isVisible = visible
    }

    private fun setFixedBugsCardViewVisibility(visible: Boolean) {
        var animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse)
        if (!visible) {
            animation = AnimationUtils.loadAnimation(this, R.anim.rotate_180)
        }

        b.imgClose3.animation = animation

        b.tvFixedBug1.isVisible = visible
        b.tvFixedBug2.isVisible = visible
    }
}