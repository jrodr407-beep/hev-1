package com.example.hevsuit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreensaverActivity : AppCompatActivity() {

    private lateinit var timeDisplay: TextView
    private lateinit var handler: Handler
    private lateinit var timeRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screensaver)

        timeDisplay = findViewById(R.id.timeDisplay)
        val closeButton = findViewById(R.id.closeScreensaver)

        // Close button
        closeButton.setOnClickListener {
            finish()
        }

        // Update time every second
        handler = Handler(Looper.getMainLooper())
        timeRunnable = object : Runnable {
            override fun run() {
                updateTime()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timeRunnable)
    }

    private fun updateTime() {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeDisplay.text = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeRunnable as Runnable)
    }
}
