package edu.us.ischool.guessemall


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        cardBody = card.findViewById(R.id.card_body)
        name = card.findViewById(R.id.name)
        pokeball = card.findViewById(R.id.pokeball)
        number = card.findViewById(R.id.number)
        sprite = card.findViewById(R.id.sprite)
    }

    fun bindModel(item: Pokemon) {
        // getting data values for topic
        name!!.text = item.name[0].uppercase() + item.name.substring(1)
        // get correct pokeball and sprite icon
        if (item.caught == 0) {
            pokeball?.setImageResource(R.drawable.undiscovered_pokeball)
            sprite?.setImageResource(R.drawable.undiscovered)
        } else {
            pokeball?.setImageResource(R.drawable.mini_pokeball)
            fetchSprite(item.sprite, sprite!!)
        }
        number?.text = formatNumber(item.number.toString())
        //sprite.setImageBitmap()
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

    // get pokemon image from designated URL
    fun fetchSprite(url: String, imgView: ImageView) {
        val executor = Executors.newSingleThreadExecutor()
        var image: Bitmap? = null
        executor.execute {
            try {
                val `in` = URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)
                imgView.setImageBitmap(image)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        holder.bindModel(pokemon[position])

        // setting listener
        holder.cardBody?.setOnClickListener{
            val intent = Intent(activity, PokedexEntry::class.java)

            // adding extras to intent
            intent.putExtra("EXTRA_INDEX", position)

            activity.startActivity(intent)
        }
    }
}