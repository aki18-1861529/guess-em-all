package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // start Game activity on Start Game button tap
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val intent = Intent(this, game::class.java)
            startActivity(intent)
        }
    }
}