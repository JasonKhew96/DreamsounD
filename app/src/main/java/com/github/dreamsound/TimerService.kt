package com.github.dreamsound

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class TimerService : Service(), MediaPlayer.OnPreparedListener, SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private var mLight: Sensor? = null
    private var mOrient: Sensor? = null
    private var mGZ = 0f
    private var mEventCountSinceGZChanged = 0
    private val MAX_COUNT_GZ_CHANGE = 10

    var sensor1 = false
    var sensor2 = false

    private var mMediaPlayer: MediaPlayer? = null
    private var minutes = 1
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var cdt: CountDownTimer

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val lux = event!!.values[0]
            if (lux < 5) {
                sensor1 = true
            }
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val gz = event.values[2]
            if (mGZ === 0f) {
                mGZ = gz
            } else {
                if (mGZ * gz < 0) {
                    mEventCountSinceGZChanged++
                    if (mEventCountSinceGZChanged === MAX_COUNT_GZ_CHANGE) {
                        mGZ = gz
                        mEventCountSinceGZChanged = 0
                        if (gz > 0) {
                            Log.d(this::class.java.simpleName, "now screen is facing up.")
                        } else if (gz < 0) {
                            Log.d(this::class.java.simpleName, "now screen is facing down.")
                            sensor2 = true
                        }
                    }
                } else {
                    if (mEventCountSinceGZChanged > 0) {
                        mGZ = gz
                        mEventCountSinceGZChanged = 0
                    }
                }
            }
        }
        Log.d(this::class.java.simpleName, "$sensor1 $sensor2")
        if (sensor1 && sensor2) {
            onDestroy()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLight?.also { light ->
            mSensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
        mOrient?.also { orient ->
            mSensorManager.registerListener(this, orient, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (intent != null) {
            minutes = intent.getIntExtra("minutes", 60)
            Log.i(this::class.java.simpleName, "intent: $minutes")
        }
        cdt = object : CountDownTimer((minutes * 60 * 1000).toLong(), 1000) {
            override fun onFinish() {
                Log.i(this::class.java.simpleName, "Timer finished")
                onDestroy()
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.i(this::class.java.simpleName, "Countdown seconds remaining: " + millisUntilFinished / 1000)
                bi.putExtra("countdown", millisUntilFinished)
                sendBroadcast(bi)
            }

        }
        cdt.start()

        val afd = assets.openFd("Xeuphoria - Midnight.mp3")
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            isLooping = true
            setOnPreparedListener(this@TimerService)
            prepareAsync() // prepare async to not block main thread
        }


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mOrient = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onDestroy() {
        sensor1 = false
        sensor2 = false
        cdt.cancel()
        if (mMediaPlayer!!.isPlaying) mMediaPlayer?.stop()
        mSensorManager.unregisterListener(this)
        bi.putExtra("stop", true)
        sendBroadcast(bi)
        super.onDestroy()
    }

    companion object {
        const val COUNTDOWN_BR = "com.github.countdown_br"
        var bi = Intent(COUNTDOWN_BR)
    }
}
