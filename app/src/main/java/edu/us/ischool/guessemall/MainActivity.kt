package edu.us.ischool.guessemall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // do something here maybe
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            ),
            1
        )

        // macgyver fix for crash due to missing pokelist
        findViewById<Button>(R.id.btnStartGame).isEnabled = false
        findViewById<Button>(R.id.btnPokedex).isEnabled = false
        Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_SHORT).show()
        val messageTimer = Timer()
        messageTimer.schedule(object : TimerTask() {
            override fun run() {
                if (App.data.isInitialized) {
                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        findViewById<Button>(R.id.btnStartGame).isEnabled = true
                        findViewById<Button>(R.id.btnPokedex).isEnabled = true
                        Toast.makeText(applicationContext, "Finished loading", Toast.LENGTH_SHORT).show()
                    })
                    this.cancel()
                }
            }
        }, 0, 250)

        // update list of caught pokemon on boot
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        var caughtSet = sharedPreference.getStringSet("caught", mutableSetOf<String>())
        if (caughtSet != null) {
            App.data.refreshCaught(caughtSet)
        }

        // start Game activity on Start Game button tap
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Start Pokedex activity on click of Pokedex button
        findViewById<Button>(R.id.btnPokedex).setOnClickListener {
            val intent = Intent(this, Pokedex::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Start GameStats activity on click of Game Statistics button
        findViewById<Button>(R.id.btnGameStats).setOnClickListener {
            val intent = Intent(this, GameStats::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}