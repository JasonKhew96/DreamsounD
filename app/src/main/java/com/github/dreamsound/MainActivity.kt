package com.github.dreamsound

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val format = DateTimeFormatter.ofPattern("hh:mm a")
        val time = LocalDateTime.now().toLocalTime().format(format)
//        Toast.makeText(this, time, Toast.LENGTH_LONG).show()
        clockView.text = time
    }

    fun start(view: View) {
        val intent = Intent(this, ChooseActivity::class.java)
        startActivity(intent)
    }
}
