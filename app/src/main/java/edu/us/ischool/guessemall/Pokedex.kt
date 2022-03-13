package edu.us.ischool.guessemall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Pokedex : AppCompatActivity() {
    lateinit var pokeGrid: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        var caughtSet = sharedPreference.getStringSet("caught", mutableSetOf<String>())
        var caughtCount = caughtSet!!.size

        // idk how to format a string resource
        val countText = findViewById<TextView>(R.id.pokeCount)
        countText.text = "You have discovered " + caughtCount + " out of 898 Pokemon!"

        // bind adapter to the RecyclerView class
        pokeGrid = findViewById(R.id.pokegrid)
        val adapter = GridAdapter(this)
        pokeGrid.adapter = adapter

        val layoutManager = GridLayoutManager(this, 4)
        pokeGrid.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}