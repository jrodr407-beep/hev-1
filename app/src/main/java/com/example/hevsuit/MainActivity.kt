package com.example.hevsuit

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var isHelmetView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // Set up button click listeners
        setupButtons()
    }

    private fun setupButtons() {
        // Toggle button (only in portrait mode)
        val toggleButton = findViewById<Button>(R.id.toggleButton)
        if (toggleButton.visibility != View.GONE) {
            toggleButton.setOnClickListener {
                val buttonPanel = findViewById<LinearLayout>(R.id.buttonPanel)
                if (buttonPanel.visibility == View.VISIBLE) {
                    buttonPanel.visibility = View.GONE
                } else {
                    buttonPanel.visibility = View.VISIBLE
                }
                vibrateShort()
            }
        }

        // Button 1
        findViewById<Button>(R.id.buttonMarkIV).setOnClickListener {
            playAudio(R.raw.hev_mark_iv)
            vibrateSOS()
            flashRed()
        }

        // Button 2
        findViewById<Button>(R.id.buttonLacerations).setOnClickListener {
            playAudio(R.raw.hev_minor_lacerations_detected)
            vibrateSOS()
            flashRed()
        }

        // Button 3
        findViewById<Button>(R.id.buttonFracture).setOnClickListener {
            playAudio(R.raw.major_fracture_detected)
            vibrateSOS()
            flashRed()
        }

        // Button 4
        findViewById<Button>(R.id.buttonDeathImminent).setOnClickListener {
            playAudio(R.raw.half_life_hev_user_death_imminent)
            vibrateSOS()
            flashRed()
        }

        // Button 5 - Beep beep beep
        findViewById<Button>(R.id.buttonBeep).setOnClickListener {
            playAudio(R.raw.hev_suit_beep_beep_beep)
            vibrateSOS()
            flashRed()
        }

        // View toggle button
        findViewById<Button>(R.id.viewToggle).setOnClickListener {
            toggleView()
            vibrateShort()
        }
    }

    private fun toggleView() {
        val suitImage = findViewById<ImageView>(R.id.hevSuitImage)
        isHelmetView = !isHelmetView

        if (isHelmetView) {
            suitImage.setImageResource(R.drawable.hev_helm)
        } else {
            suitImage.setImageResource(R.drawable.neon_orange_lambda_armor_blueprint)
        }
    }

    private fun flashRed() {
        val redFlash = findViewById<View>(R.id.redFlash)
        redFlash.visibility = View.VISIBLE

        // Fade out animation
        val fadeOut = ObjectAnimator.ofFloat(redFlash, "alpha", 0.8f, 0f)
        fadeOut.duration = 300
        fadeOut.start()

        // Hide after animation
        Handler(Looper.getMainLooper()).postDelayed({
            redFlash.visibility = View.GONE
            redFlash.alpha = 0.8f
        }, 300)
    }

    private fun playAudio(resourceId: Int) {
        // Release any existing MediaPlayer
        mediaPlayer?.release()

        // Create new MediaPlayer
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer?.start()

        // Set completion listener to release resources
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
        }
    }

    private fun vibrateSOS() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // SOS pattern: ... --- ... (short-short-short, long-long-long, short-short-short)
                // Morse code: dot=1 unit, dash=3 units, gap=1 unit, letter gap=3 units
                val timings = longArrayOf(
                    0,    // start immediately
                    100,  // S - first dot (1 unit)
                    100,  // gap (1 unit)
                    100,  // S - second dot (1 unit)
                    100,  // gap (1 unit)
                    100,  // S - third dot (1 unit)
                    300,  // letter gap (3 units)
                    300,  // O - first dash (3 units)
                    100,  // gap (1 unit)
                    300,  // O - second dash (3 units)
                    100,  // gap (1 unit)
                    300,  // O - third dash (3 units)
                    300,  // letter gap (3 units)
                    100,  // S - first dot (1 unit)
                    100,  // gap (1 unit)
                    100,  // S - second dot (1 unit)
                    100,  // gap (1 unit)
                    100   // S - third dot (1 unit)
                )
                val amplitudes = intArrayOf(
                    0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255
                )
                it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                // SOS pattern: ... --- ...
                val pattern = longArrayOf(
                    0, 100, 100, 100, 100, 100, 300,  // S and letter gap
                    300, 100, 300, 100, 300, 300,       // O and letter gap
                    100, 100, 100, 100, 100             // S
                )
                it.vibrate(pattern, -1)
            }
        }
    }

    private fun vibrate() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Three strong pulses: BEEP BEEP BEEP
                val timings = longArrayOf(0, 150, 100, 150, 100, 150)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                // Three strong pulses pattern
                val pattern = longArrayOf(0, 150, 100, 150, 100, 150)
                it.vibrate(pattern, -1)
            }
        }
    }

    private fun vibrateShort() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Short single vibration for toggle
                it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Release MediaPlayer when activity is paused
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator = null
    }
}
