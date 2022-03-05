package edu.us.ischool.guessemall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PokedexEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex_entry)

        // getting intent from last activity and pokemon data
        val pIntent = this.intent
        val pokemon: Pokemon? = pIntent.extras?.getInt("EXTRA_INDEX")
            ?.let { App.data.getPokemon(it) }

        // setting page contents
        val nameLabel: TextView = findViewById(R.id.name)

    }
}