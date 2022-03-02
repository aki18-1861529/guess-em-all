package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner

class Game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // fill in autocomplete options
        // Get a reference to the AutoCompleteTextView in the layout
        val textView = findViewById(R.id.tvAutoComplete_Pokemon) as AutoCompleteTextView
        // Get the string array
        val countries: Array<out String> = resources.getStringArray(R.array.pokemon_array)
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries).also { adapter ->
            textView.setAdapter(adapter)
        }

        // fill in spinner options (default: Generation)
        val spinner: Spinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.generations_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        // Show results page with pokemon stats
        findViewById<Button>(R.id.btnGuessPokemon).setOnClickListener {
            val intent = Intent(this, PokemonStats::class.java)
            startActivity(intent)
        }
    }
}