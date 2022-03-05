package edu.us.ischool.guessemall

import android.os.Environment
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

// json data link https://gist.githubusercontent.com/corinzarkowski/e59261a8d5728a11793b4c38186a2924/raw/5af1cd35afc9e88b3804d7352b3c8216551ed387/pokemonlist.json
class DataRepository {
    private val pokeList: MutableList<Pokemon> = mutableListOf()

    init {
        initData()
    }

    // initialize list of pokemon data
    private fun initData() {
        val file = File(Environment.getExternalStorageDirectory().path, "pokemon.json")

        // if the file exists, create list of Pokemon
        if (file.exists()) {
            val fileText = file.inputStream().readBytes().toString(Charsets.UTF_8)

            val data = JSONArray(fileText)
            for (i in 0..(data.length() - 1)) {
                val pokemonObj = data.getJSONObject(i)
                val typeListObj = pokemonObj.getJSONArray("types")
                val typeList = arrayListOf<String>()
                for (j in 0..(typeListObj.length() - 1)) {
                    typeList.add(typeListObj[j] as String)
                }
                pokeList.add(Pokemon(
                    pokemonObj.getString("name"),
                    pokemonObj.getInt("id"),
                    pokemonObj.getString("description"),
                    pokemonObj.getString("height"),
                    pokemonObj.getString("weight"),
                    pokemonObj.getString("sprite"),
                    typeList,
                    1 // CHANGED FOR TESTING PURPOSES
                ))
            }
        } else {
            downloadJSON()
        }
    }

    // download data from JSON file
    private fun downloadJSON() {
        val t = thread {
            val server = URL("https://gist.githubusercontent.com/corinzarkowski/e59261a8d5728a11793b4c38186a2924/raw/5af1cd35afc9e88b3804d7352b3c8216551ed387/pokemonlist.json")
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
        }
    }

    fun getAllPokemon() : MutableList<Pokemon> {
        return pokeList
    }

    fun getPokemon(index: Int) : Pokemon {
        return pokeList[index]
    }
}

// added an extra image field since my app has an image for each topic
data class Pokemon(
    val name: String, val number: Int, var desc: String, var height: String, var weight: String,
    var sprite: String, var types: List<String>, val caught: Int) : Serializable
