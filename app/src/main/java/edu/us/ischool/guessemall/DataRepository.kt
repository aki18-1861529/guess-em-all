package edu.us.ischool.guessemall

import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

// json data link https://gist.githubusercontent.com/LukeDors/7a205e5f0458ed4ac5c265efc5c2e69b/raw/572d9ec2f26a130c2ddc02c764ebe75935898b31/pokemon.json
class DataRepository {
    private lateinit var pokeMap: HashMap<String, Pokemon>
    private lateinit var pokeList: MutableList<Pokemon>
    var isInitialized: Boolean = false

    fun lateInit() {
        pokeList = mutableListOf()
        pokeMap = hashMapOf()
        downloadJSON()
    }

    // initialize list of pokemon data
    private fun initData() {
        val file = File(Environment.getExternalStorageDirectory().path, "pokemon.json")

        // if the file exists, create list of Pokemon
        if (file.exists() && pokeList.isEmpty()) {
            Log.i("TESTING", "initializing")
            val fileText = file.inputStream().readBytes().toString(Charsets.UTF_8)

            val data = JSONArray(fileText)
            for (i in 0..(data.length() - 1)) {
                val pokemonObj = data.getJSONObject(i)
                val typeListObj = pokemonObj.getJSONArray("types")
                val typeList = arrayListOf<String>()
                for (j in 0..(typeListObj.length() - 1)) {
                    typeList.add(typeListObj[j] as String)
                }

                var name = pokemonObj.getString("name")[0].uppercase() + pokemonObj.getString("name").substring(1)
                var pokemon : Pokemon = Pokemon(
                    name,
                    pokemonObj.getInt("id"),
                    pokemonObj.getString("description").replace("\n"," "),
                    pokemonObj.getInt("height"),
                    pokemonObj.getInt("weight"),
                    pokemonObj.getString("sprite"),
                    typeList,
                    pokemonObj.getInt("caught"),
                    pokemonObj.getInt("evolutionstage")
                )

                pokeList.add(pokemon)
                pokeMap[name] = pokemon
            }
            isInitialized = true
        }
    }

    // download data from JSON file
    private fun downloadJSON() {
        val t = thread {
            Log.i("TESTING", "downloading JSON")
            val server = URL("https://gist.githubusercontent.com/LukeDors/7a205e5f0458ed4ac5c265efc5c2e69b/raw/572d9ec2f26a130c2ddc02c764ebe75935898b31/pokemon.json")
            val client: HttpURLConnection = server.openConnection() as HttpURLConnection
            client.requestMethod = "GET"
            val filePath = File(Environment.getExternalStorageDirectory().path, "pokemon.json")
            val result = BufferedReader(InputStreamReader(client.inputStream))
            val output = filePath.bufferedWriter(Charsets.UTF_8, DEFAULT_BUFFER_SIZE)
            var inputLine: String?
            while (result.readLine().also { inputLine = it } !== null) {
                output.write(inputLine)
                output.newLine()
            }
            result.close()
            output.close()
            initData()
        }
    }

    fun getAllPokemon() : MutableList<Pokemon> {
        return pokeList
    }

    fun getPokemon(name: String) : Pokemon? {
        return pokeMap[name]
    }

    fun getAllNames() : Array<String> {
        return pokeMap.keys.toTypedArray()
    }

    fun addAsCaught(id : Int) {
        pokeList[id - 1].caught = 1
    }

    fun refreshCaught(caughtSet : MutableSet<String>) {
        caughtSet.forEach {
            pokeList[it.toInt() - 1].caught = 1
        }
    }
}

// added an extra image field since my app has an image for each topic
data class Pokemon(
    val name: String, val number: Int, var desc: String, var height: Int, var weight: Int,
    var sprite: String, var types: List<String>, var caught: Int, var evos: Int) : Serializable
