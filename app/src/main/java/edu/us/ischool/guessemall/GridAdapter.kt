package edu.us.ischool.guessemall


import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// importing data from another class

class GridHolder(row: View) : RecyclerView.ViewHolder(row) {
    var nameLabel: TextView? = null
    var cardBody: LinearLayout? = null

    init {
        nameLabel = row.findViewById<Button>(R.id.name)
        cardBody = row.findViewById(R.id.card_body)
    }

    fun bindModel(item: String) {
        // getting data values for topic
        nameLabel!!.text = item
    }
}

class GridAdapter(val activity: Activity) : RecyclerView.Adapter<GridHolder>() {
    private val pokemon = arrayOf("Pikachu", "Bulbasaur", "Charmander", "Squirtle", "Ampharos", "Cacturne", "Pinsr", "Appletun", "Toxtricity")
    override fun getItemCount() : Int { return pokemon.size }

    // recycler view inside activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GridHolder {
        return GridHolder(
            // displays the row
            activity.layoutInflater.inflate(R.layout.card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        // asks row holder to bind this data into its UI
        holder.bindModel(pokemon[position])

        // setting listener
        holder.cardBody?.setOnClickListener{
            val intent = Intent(activity, PokedexEntry::class.java)

            // adding extras to intent
            intent.putExtra("EXTRA_NAME", holder.nameLabel?.text)

            Log.i("MainActivity", intent.extras.toString())
            activity.startActivity(intent)
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}