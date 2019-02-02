package com.github.dreamsound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
    }

    fun onChoose(view: View) {
        val btn: Button = view as Button
        Toast.makeText(this, btn.text, Toast.LENGTH_LONG).show()
        startPlayer()
    }

    private fun startPlayer() {
        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
    }
}
