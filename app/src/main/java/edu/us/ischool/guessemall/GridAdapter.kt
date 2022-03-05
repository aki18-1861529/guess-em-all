package edu.us.ischool.guessemall


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.net.URL
import java.util.concurrent.Executors
import java.util.logging.Handler

// importing data from another class

class GridHolder(card: View) : RecyclerView.ViewHolder(card) {
    var cardBody: LinearLayout? = null
    var name: TextView? = null
    var pokeball: ImageView?  = null
    var number: TextView? = null
    var sprite: ImageView? = null

    init {
        cardBody = card.findViewById(R.id.cardBody)
        name = card.findViewById(R.id.tvName)
        pokeball = card.findViewById(R.id.imgPokeball)
        number = card.findViewById(R.id.tvNumber)
        sprite = card.findViewById(R.id.imgSprite)
    }

    fun bindModel(item: Pokemon, activity: Activity) {
        // getting data values for topic
        name!!.text = item.name[0].uppercase() + item.name.substring(1)
        // get correct pokeball and sprite icon
        if (item.caught == 0) {
            pokeball?.setImageResource(R.drawable.undiscovered_pokeball)
            sprite?.setImageResource(R.drawable.undiscovered)
        } else {
            pokeball?.setImageResource(R.drawable.mini_pokeball)
            // used third party library Glide to parse and display url images
            Glide.with(activity).load(item.sprite).into(sprite!!)
        }
        number?.text = formatNumber(item.number.toString())
    }

    // format pokemon number string to be "No. XXX"
    fun formatNumber(num: String) : String {
        var result: String? = num
        if (num.length == 1) {
            result = "00${num}"
        } else if (num.length == 2) {
            result = "0${num}"
        }

        return "No. $result"
    }
}

class GridAdapter(val activity: Activity) : RecyclerView.Adapter<GridHolder>() {
    private val pokemon = App.data.getAllPokemon()
    override fun getItemCount() : Int { return pokemon.size }

    // recycler view inside activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GridHolder {
        return GridHolder(
            // displays the grid
            activity.layoutInflater.inflate(R.layout.card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        // asks row holder to bind this data into its UI
        holder.bindModel(pokemon[position], activity)

        // setting listener
        holder.cardBody?.setOnClickListener{
            if (pokemon[position].caught == 0) {
                // lol can't get toast to work
                Toast.makeText(it.context, "Pokemon not discovered!", Toast.LENGTH_SHORT).show()
                Log.i("Data", "Not discovered")
            } else {
                val intent = Intent(activity, PokedexEntry::class.java)

                // adding extras to intent
                intent.putExtra("EXTRA_INDEX", position)

                Log.i("MainActivity", intent.extras.toString())
                activity.startActivity(intent)
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}