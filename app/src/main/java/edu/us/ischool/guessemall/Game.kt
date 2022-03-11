package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.*
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class Game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val tStart = System.currentTimeMillis()

        // pick random pokemon
        val pokemonList = App.data.getAllPokemon()
        val pokemon = pokemonList[Random.nextInt(0, 150)]
        Log.i("TESTING", pokemon.toString())

        val dateSeed = LocalDateTime.now().dayOfYear + LocalDateTime.now().year
        val dailyPokemon = pokemonList[Random(dateSeed).nextInt(0, 150)]
        Log.i("TESTING", dailyPokemon.toString())

        // fill in autocomplete options
        // Get a reference to the AutoCompleteTextView in the layout
        val textView = findViewById(R.id.tvAutoComplete_Pokemon) as AutoCompleteTextView
        // Get the string array through DataRepository class
        val data = DataRepository()
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data.getAllNames()).also { adapter ->
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

        Log.i("Guess-Em-All", dailyPokemon.name.replaceFirstChar { it.titlecase() })

        //Guess a Pokemon Attribute
        findViewById<Button>(R.id.btnGuessPart).setOnClickListener {
            val partBtn = findViewById<Button>(R.id.btnGuessPart)
            val spinnerAnswer = findViewById<Spinner>(R.id.spinner).getSelectedItem().toString()
            if (partBtn.text == "Guess Type 1" && spinnerAnswer == dailyPokemon.types[0].replaceFirstChar { it.titlecase() }) {
                Toast.makeText(this, "Correct Type 1", Toast.LENGTH_SHORT).show()
            } else if (partBtn.text == "Guess Type 2" && spinnerAnswer == "None" && dailyPokemon.types.size == 1) {
                Toast.makeText(this, "Correct Type 2 (Single Typing)", Toast.LENGTH_SHORT).show()
            } else if (partBtn.text == "Guess Type 2" && spinnerAnswer == dailyPokemon.types[1].replaceFirstChar { it.titlecase() }) {
                Toast.makeText(this, "Correct Type 2", Toast.LENGTH_SHORT).show()
            } else if (partBtn.text == "Guess Evolution" && spinnerAnswer.last().toString() == dailyPokemon.evos.toString()) {
                Toast.makeText(this, "Correct Number of Evolutions", Toast.LENGTH_SHORT).show()
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen I: Red/Blue/Yellow") {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Incorrect Guess, Try Again", Toast.LENGTH_SHORT).show()
            }
        }

        // Show results page with pokemon stats
        findViewById<Button>(R.id.btnGuessPokemon).setOnClickListener {
            // check if the guessed pokemon is correct
            if (dailyPokemon.name.replaceFirstChar { it.titlecase() } == textView.text.toString()) {
                // calculate elapsed time in seconds
                val tEnd = System.currentTimeMillis()
                val tDelta = tEnd - tStart
                val elapsedSeconds = tDelta / 1000.0

                val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
                var editor = sharedPreference.edit()

                // increment total games and time
                var total: Int = 1
                var totalTime: Long = elapsedSeconds.toLong()
                if (sharedPreference.getInt("totalGames", 0) != 0) {
                    total = sharedPreference.getInt("totalGames", 0).inc()
                }
                if (sharedPreference.getLong("totalTime", 0) != 0L) {
                    totalTime = sharedPreference.getLong("totalTime", 0) + elapsedSeconds.toLong()
                }
                editor.putInt("totalGames", total)
                editor.putLong("totalTime", totalTime)

                // calculate average time
                if (total > 1L) {
                    editor.putLong("avgTime", totalTime.div(total))
                } else {
                    editor.putLong("avgTime", totalTime)
                }

                // update best time
                if (sharedPreference.getLong("bestTime", 0) != 0L) {
                    var currBestTime = sharedPreference.getLong("bestTime", 0)
                    if (elapsedSeconds < currBestTime) {
                        editor.putLong("bestTime", elapsedSeconds.toLong())
                    }
                } else {
                    editor.putLong("bestTime", elapsedSeconds.toLong())
                }

                // update slowest time
                if (sharedPreference.getLong("slowestTime", 0) != 0L) {
                    var currSlowestTime = sharedPreference.getLong("slowestTime", 0)
                    if (elapsedSeconds > currSlowestTime) {
                        editor.putLong("slowestTime", elapsedSeconds.toLong())
                    }
                } else {
                    editor.putLong("slowestTime", elapsedSeconds.toLong())
                }

                editor.commit()

                dailyPokemon.caught = 1
                val intent = Intent(this, PokedexEntry::class.java)
                intent.putExtra("pokemon", textView.text.toString())
                intent.putExtra("time", DateUtils.formatElapsedTime(elapsedSeconds.toLong()))
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                Toast.makeText(this, "Incorrect Guess, Try Again", Toast.LENGTH_SHORT).show()
            }
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