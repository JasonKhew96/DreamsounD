package com.github.dreamsound

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_player.*
import org.jetbrains.anko.*


class PlayerActivity : AppCompatActivity() {
    private var countDownMinutes: Int = 60
    private var isStart = false
    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                updateUI(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(TimerService.COUNTDOWN_BR))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(br)
    }

    override fun onStop() {
        try {
            unregisterReceiver(br)
        } catch (e: Exception) {

        }
        super.onStop()
    }

    override fun onDestroy() {
//        stopService(Intent(this, TimerService::class.java))
        super.onDestroy()
    }

    fun updateUI(intent: Intent) {
        if (intent.extras != null) {
            val millisUntilFinished = intent.getLongExtra("countdown", 0)
            val stop = intent.getBooleanExtra("stop", false)
            if (millisUntilFinished != 0L) {
                setClock.text = (millisUntilFinished / 1000).toString() + " seconds"
                onSet()
            }
            if (stop) {
                onUnset()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        onUnset()

        btnStartStop.setOnClickListener {
            btnStartStop.isEnabled = false
            if (isStart) {
                onUnset()
                Log.i(this::class.java.simpleName, "Timer stop")
                stopService(Intent(this, TimerService::class.java))
            } else {
                onSet()
                Log.i(this::class.java.simpleName, "Timer start")
                val intent = Intent(this, TimerService::class.java).apply {
                    putExtra("minutes", countDownMinutes)
                }
                startService(intent)
            }
            Handler().postDelayed(Runnable {
                // This method will be executed once the timer is over
                btnStartStop.isEnabled = true
            }, 2000)
        }
    }

    private fun onSet() {
        btnStartStop.text = "stop"
        playerText.text = "Touch or flip the phone to stop"
        setClock.setOnClickListener { }
        isStart = true
    }

    private fun onUnset() {
        btnStartStop.text = "start"
        playerText.text = "Set time"
        setClock.setOnClickListener {
            showTimePickerDialog()
        }
        isStart = false
        setClock.text = countDownMinutes.toString() + " mins"
    }

    private fun showTimePickerDialog() {
        alert {
            customView {
                title = "Countdown time (minutes)"
                val minutes = numberPicker {
                    minValue = 1
                    maxValue = 120
                    value = 60
                }
                okButton {
                    countDownMinutes = minutes.value
                    setClock.text = countDownMinutes.toString() + " mins"
                }
                cancelButton { }
            }
        }.show()
    }

}
