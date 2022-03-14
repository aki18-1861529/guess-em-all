package edu.us.ischool.guessemall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.widget.*
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class Game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // remove back button from action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val tStart = System.currentTimeMillis()

        val pokemonList = App.data.getAllPokemon()
        val mode : String = intent.getStringExtra("MODE").toString()
        var pokemon : Pokemon
        if (mode == "daily") {
            // pick daily
            val dateSeed = LocalDateTime.now().dayOfYear + LocalDateTime.now().year
            pokemon = pokemonList[Random(dateSeed).nextInt(0, 897)]
            Log.i("TESTING", pokemon.toString())
        } else {
            // pick random pokemon
            pokemon = pokemonList[Random.nextInt(0, 897)]
            Log.i("TESTING", pokemon.toString())
        }

        // fill in autocomplete options
        // Get a reference to the AutoCompleteTextView in the layout
        val tvAutoCompletePokemon = findViewById<AutoCompleteTextView>(R.id.tvAutoComplete_Pokemon)
        // Get the string array through DataRepository class
        val data = App.data
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data.getAllNames()).also { adapter ->
            tvAutoCompletePokemon.setAdapter(adapter)
        }

        // disable Guess Pokemon button if no Pokemon name is given to guess
        val btnGuessPokemon = findViewById<Button>(R.id.btnGuessPokemon)
        tvAutoCompletePokemon.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                if (s.toString().trim({ it <= ' ' }).isEmpty())
                {
                    btnGuessPokemon.setEnabled(false)
                }
                else
                {
                    btnGuessPokemon.setEnabled(true)
                }
            }
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int,
                                           after:Int) {
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

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

        Log.i("Guess-Em-All", pokemon.name.replaceFirstChar { it.titlecase() })

        //Guess a Pokemon Attribute
        findViewById<Button>(R.id.btnGuessPart).setOnClickListener {
            val partBtn = findViewById<Button>(R.id.btnGuessPart)
            val spinnerAnswer = findViewById<Spinner>(R.id.spinner).getSelectedItem().toString()
            if (partBtn.text == "Guess Type 1" && spinnerAnswer == pokemon.types[0].replaceFirstChar { it.titlecase() }) {
                Toast.makeText(this, "Correct Type 1", Toast.LENGTH_SHORT).show()
                val newImg = "correct_type_" + pokemon.types[0]
                findViewById<ImageView>(R.id.imgGuessType1).setImageResource(resources.getIdentifier(newImg, "drawable", this.packageName))
            } else if (partBtn.text == "Guess Type 2" && spinnerAnswer == "None" && pokemon.types.size == 1) {
                Toast.makeText(this, "Correct Type 2 (Single Typing)", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessType2).setImageResource(R.drawable.correct_type_typeless)
            } else if (partBtn.text == "Guess Type 2" && spinnerAnswer != "None" && pokemon.types.size == 1) {
                Toast.makeText(this, "Incorrect Guess, Try Again", Toast.LENGTH_SHORT).show()
            } else if (partBtn.text == "Guess Type 2" && spinnerAnswer == pokemon.types[1].replaceFirstChar { it.titlecase() }) {
                Toast.makeText(this, "Correct Type 2", Toast.LENGTH_SHORT).show()
                val newImg = "correct_type_" + pokemon.types[1]
                findViewById<ImageView>(R.id.imgGuessType2).setImageResource(resources.getIdentifier(newImg, "drawable", this.packageName))
            } else if (partBtn.text == "Guess Evolution" && spinnerAnswer.last().toString() == pokemon.evos.toString()) {
                Toast.makeText(this, "Correct Stage of Evolution", Toast.LENGTH_SHORT).show()
                val newImg = "correct_evo_" + pokemon.evos.toString()
                findViewById<ImageView>(R.id.imgGuessEvo).setImageResource(resources.getIdentifier(newImg, "drawable", this.packageName))
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen I: Red/Blue/Yellow" && pokemon.number <= 151) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_i)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen II: Gold/Silver/Crystal" && pokemon.number >= 152 && pokemon.number <= 251) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_ii)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen III: Ruby/Sapphire/Emerald" && pokemon.number >= 252 && pokemon.number <= 386) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_iii)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen IV: Diamond/Pearl/Platinum" && pokemon.number >= 387 && pokemon.number <= 493) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_iv)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen V: Black/White" && pokemon.number >= 494 && pokemon.number <= 649) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_v)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen VI: X/Y" && pokemon.number >= 650 && pokemon.number <= 721) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_vi)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen VII: Sun/Moon" && pokemon.number >= 722 && pokemon.number <= 809) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_vii)
            } else if (partBtn.text == "Guess Generation" && spinnerAnswer == "Gen VIII: Sword/Shield" && pokemon.number >= 810 && pokemon.number <= 898) {
                Toast.makeText(this, "Correct Generation", Toast.LENGTH_SHORT).show()
                findViewById<ImageView>(R.id.imgGuessGen).setImageResource(R.drawable.correct_gen_viii)
            } else {
                Toast.makeText(this, "Incorrect Guess, Try Again", Toast.LENGTH_SHORT).show()
            }
        }

        // Show results page with pokemon stats
        btnGuessPokemon.setOnClickListener {
            // check if the guessed pokemon is correct
            if (pokemon.name.replaceFirstChar { it.titlecase() } == tvAutoCompletePokemon.text.toString()) {
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

                // update best time and fastest found pokemon
                if (sharedPreference.getLong("bestTime", 0) != 0L) {
                    var currBestTime = sharedPreference.getLong("bestTime", 0)
                    if (elapsedSeconds < currBestTime) {
                        editor.putLong("bestTime", elapsedSeconds.toLong())
                        editor.putString("fastestFound", pokemon.name.replaceFirstChar { it.titlecase() })
                    }
                } else {
                    editor.putLong("bestTime", elapsedSeconds.toLong())
                    editor.putString("fastestFound", pokemon.name.replaceFirstChar { it.titlecase() })
                }

                // update slowest time
                if (sharedPreference.getLong("slowestTime", 0) != 0L) {
                    var currSlowestTime = sharedPreference.getLong("slowestTime", 0)
                    if (elapsedSeconds > currSlowestTime) {
                        editor.putLong("slowestTime", elapsedSeconds.toLong())
                        editor.putString("slowestFound", pokemon.name.replaceFirstChar { it.titlecase() })
                    }
                } else {
                    editor.putLong("slowestTime", elapsedSeconds.toLong())
                    editor.putString("slowestFound", pokemon.name.replaceFirstChar { it.titlecase() })
                }

                // update caught pokemon
                var caughtSet = sharedPreference.getStringSet("caught", mutableSetOf<String>())
                if (caughtSet != null) {
                    if (!caughtSet.contains(pokemon.number.toString())) {
                        Log.i("TESTING", caughtSet.toString())
                        caughtSet.add(pokemon.number.toString())
                        App.data.addAsCaught(pokemon.number)
                        editor.putStringSet("caught", caughtSet)
                    }
                }

                // update dailies if applicable
                if (mode == "daily") {
                    var dailySet = sharedPreference.getStringSet("dailies", mutableSetOf<String>())
                    if (dailySet != null) {
                        val dateSeed = LocalDateTime.now().dayOfYear + LocalDateTime.now().year
                        dailySet.add(dateSeed.toString())
                        editor.putStringSet("dailies", dailySet)
                    }
                }

                editor.commit()

                val intent = Intent(this, PokedexEntry::class.java)
                intent.putExtra("EXTRA_NAME", tvAutoCompletePokemon.text.toString())
                intent.putExtra("time", DateUtils.formatElapsedTime(elapsedSeconds.toLong()))
                startActivity(intent)
                this.finish()
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