package com.example.hevsuit

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize shared preferences
        sharedPrefs = getSharedPreferences("HEVSuitPrefs", Context.MODE_PRIVATE)

        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // Play HEV Mark IV startup sound only on first app launch
        val isFirstLaunch = sharedPrefs.getBoolean("is_first_launch", true)
        if (isFirstLaunch) {
            playAudio(R.raw.hev_mark_iv)
            vibrate()
            sharedPrefs.edit().putBoolean("is_first_launch", false).apply()
        }

        // Set up button click listeners
        setupButtons()
    }

    private fun setupButtons() {
        // HEV Mark IV button
        findViewById<Button>(R.id.buttonMarkIV).setOnClickListener {
            playAudio(R.raw.hev_mark_iv)
            vibrate()
        }

        // Minor Lacerations button
        findViewById<Button>(R.id.buttonLacerations).setOnClickListener {
            playAudio(R.raw.hev_minor_lacerations_detected)
            vibrate()
        }

        // Major Fracture button
        findViewById<Button>(R.id.buttonFracture).setOnClickListener {
            playAudio(R.raw.major_fracture_detected)
            vibrate()
        }

        // Death Imminent button
        findViewById<Button>(R.id.buttonDeathImminent).setOnClickListener {
            playAudio(R.raw.half_life_hev_user_death_imminent)
            vibrate()
        }
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

    private fun vibrate() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Vibrate for 100ms
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
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
