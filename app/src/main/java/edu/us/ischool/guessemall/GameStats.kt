package edu.us.ischool.guessemall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.TextView

class GameStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_stats)

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val totalGames = findViewById<TextView>(R.id.totalGames)
        val totalTime = findViewById<TextView>(R.id.totalTime)
        val avgTime = findViewById<TextView>(R.id.avgTime)
        val bestTime = findViewById<TextView>(R.id.bestTime)
        val slowestTime = findViewById<TextView>(R.id.slowestTime)

        if (sharedPreference.getInt("totalGames", 0) != 0) {
            totalGames.text = sharedPreference.getInt("totalGames", 0).toString()
        }
        if (sharedPreference.getLong("totalTime", 0) != 0L) {
            totalTime.text = DateUtils.formatElapsedTime(sharedPreference.getLong("totalTime", 0))
        }
        if (sharedPreference.getLong("avgTime", 0) != 0L) {
            avgTime.text = DateUtils.formatElapsedTime(sharedPreference.getLong("avgTime", 0))
        }
        if (sharedPreference.getLong("bestTime", 0) != 0L) {
            bestTime.text = DateUtils.formatElapsedTime(sharedPreference.getLong("bestTime", 0))
        }
        if (sharedPreference.getLong("slowestTime", 0) != 0L) {
            slowestTime.text = DateUtils.formatElapsedTime(sharedPreference.getLong("slowestTime", 0))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}