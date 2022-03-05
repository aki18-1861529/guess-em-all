package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // start Game activity on Start Game button tap
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }

        // Start Pokedex activity on click of Pokedex button
        findViewById<Button>(R.id.btnPokedex).setOnClickListener {
            val intent = Intent(this, Pokedex::class.java)
            startActivity(intent)
        }

        // Start GameStats activity on click of Game Statistics button
        findViewById<Button>(R.id.btnGameStats).setOnClickListener {
            val intent = Intent(this, GameStats::class.java)
            startActivity(intent)
        }
    }
}