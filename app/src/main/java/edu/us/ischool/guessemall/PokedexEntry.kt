package edu.us.ischool.guessemall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PokedexEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex_entry)

        // getting intent from last activity
        val pIntent = this.intent

        // getting data
        val name = pIntent.extras?.getString("EXTRA_NAME")

        // setting page contents
        val nameLabel: TextView = findViewById(R.id.name_label)
        nameLabel.text = name
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}