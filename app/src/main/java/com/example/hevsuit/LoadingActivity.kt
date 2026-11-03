package com.example.hevsuit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

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

        // Play HEV Mark IV startup sound
        playAudio(R.raw.hev_mark_iv)
        vibrate()

        // Mark that startup sound has played
        sharedPrefs.edit().putBoolean("is_first_launch", false).apply()

        // Wait for audio to finish, then transition to main activity
        mediaPlayer?.setOnCompletionListener {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 500)
        }

        // Fallback: transition after 3 seconds if audio doesn't complete
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 3000)
    }

    private fun playAudio(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer?.start()
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
                val pattern = longArrayOf(0, 150, 100, 150, 100, 150)
                it.vibrate(pattern, -1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator = null
    }
}
