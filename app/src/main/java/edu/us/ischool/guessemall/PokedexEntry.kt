package edu.us.ischool.guessemall

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide

class PokedexEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex_entry)

        // remove back button from action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // getting intent from last activity and pokemon data
        val pIntent = this.intent
        val pokemon: Pokemon? = pIntent.extras?.getString("EXTRA_NAME")
            ?.let { App.data.getPokemon(it) }
        val timeVal = pIntent.extras?.getString("time")
        val newBestTime = pIntent.extras?.getBoolean("newBestTime") as Boolean

        Log.i("TESTING", pokemon.toString())

        // grabbing pokedex entry views
        val sprite: ImageView = findViewById(R.id.imgPokemonArt)
        val number: TextView = findViewById(R.id.tvPokeNumber)
        val name: TextView = findViewById(R.id.tvName)
        val height: TextView = findViewById(R.id.tvPokeHeight)
        val weight: TextView = findViewById(R.id.tvPokeWeight)
        val desc: TextView = findViewById(R.id.tvDescription)
        val type1: ImageView = findViewById(R.id.imgType1)
        val time: TextView = findViewById(R.id.time)

        if (timeVal != null) {
            time.text = time.text.toString() + " " + timeVal
            time.isVisible = true

            if (newBestTime) {
                val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .create()
                val view = layoutInflater.inflate(R.layout.custom_alert_layout, null)
                val button = view.findViewById<Button>(R.id.dialogDismiss_button)
                builder.setView(view)
                button.setOnClickListener {
                    builder.dismiss()
                }
                builder.setCanceledOnTouchOutside(false)
                builder.show()
            }
        }

        // setting view content
        // used third party library Glide to parse and display url images
        Glide.with(this).load(pokemon!!.sprite).into(sprite)
        number.text = formatNumber(pokemon!!.number.toString())
        name.text = pokemon!!.name
        height.text = "${pokemon.height.toDouble() / 10} m"
        weight.text = "${pokemon.weight.toDouble() / 10} kg"
        desc.text = pokemon.desc

        // adding the type images and handling multiple types
        type1.setImageResource(getResources().getIdentifier(pokemon.types[0], "drawable", getPackageName()));
        if (pokemon.types.size > 1) {
            val type2: ImageView = findViewById(R.id.imgType2)
            type2.setImageResource(getResources().getIdentifier(pokemon.types[1], "drawable", getPackageName()));
        }
    }

    // format pokemon number string to be "No. XXX"
    fun formatNumber(num: String) : String {
        var result: String? = num
        if (num.length === 1) {
            result = "00${num}"
        } else if (num.length === 2) {
            result = "0${num}"
        }

        return "No. $result"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}