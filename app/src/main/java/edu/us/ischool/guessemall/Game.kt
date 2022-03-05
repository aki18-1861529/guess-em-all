package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.w3c.dom.Text

class Game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // fill in autocomplete options
        // Get a reference to the AutoCompleteTextView in the layout
        val textView = findViewById(R.id.tvAutoComplete_Pokemon) as AutoCompleteTextView
        // Get the string array
        // TODO: pass in array of all pokemon names from api? instead of hard-coding all of them into strings.xml
        val pokemonNames: Array<out String> = resources.getStringArray(R.array.pokemon_array)
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pokemonNames).also { adapter ->
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
        findViewById<ImageView>(R.id.imgGuessGen).setOnClickListener {
            populateSpinner(R.array.generations_array)
            findViewById<Button>(R.id.btnGuessPart).text = "Guess Generation"
            findViewById<TextView>(R.id.tvCurrentGuessLabel).text ="Generation"
        }
        findViewById<ImageView>(R.id.imgGuessType1).setOnClickListener {
            populateSpinner(R.array.type_array)
            findViewById<Button>(R.id.btnGuessPart).text = "Guess Type 1"
            findViewById<TextView>(R.id.tvCurrentGuessLabel).text ="Type 1"
        }
        findViewById<ImageView>(R.id.imgGuessType2).setOnClickListener {
            populateSpinner(R.array.type_array)
            findViewById<Button>(R.id.btnGuessPart).text = "Guess Type 2"
            findViewById<TextView>(R.id.tvCurrentGuessLabel).text ="Type 2"
        }
        findViewById<ImageView>(R.id.imgGuessEvo).setOnClickListener {
            populateSpinner(R.array.evolution_array)
            findViewById<Button>(R.id.btnGuessPart).text = "Guess Evolution"
            findViewById<TextView>(R.id.tvCurrentGuessLabel).text ="Evolution"
        }

        // Show results page with pokemon stats
        findViewById<Button>(R.id.btnGuessPokemon).setOnClickListener {
            val intent = Intent(this,
                PokedexEntry::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun populateSpinner(contentArrayID: Int) {
        val spinner: Spinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            contentArrayID,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}