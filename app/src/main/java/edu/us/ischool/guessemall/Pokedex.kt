package edu.us.ischool.guessemall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Pokedex : AppCompatActivity() {
    lateinit var pokeGrid: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        // bind adapter to the RecyclerView class
        pokeGrid = findViewById(R.id.pokegrid)
        val adapter = GridAdapter(this)
        pokeGrid.adapter = adapter

        val layoutManager = GridLayoutManager(this, 4)
        pokeGrid.layoutManager = layoutManager
    }
}