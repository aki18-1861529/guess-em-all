package edu.us.ischool.guessemall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    App.data.lateInit()
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

        // wait for data initialization to prevent crash
        findViewById<Button>(R.id.btnStartGame).isEnabled = false
        findViewById<Button>(R.id.btnPokedex).isEnabled = false
        findViewById<Button>(R.id.btnDaily).isEnabled = false
        findViewById<TextView>(R.id.dailyDone).isVisible = false
        val messageTimer = Timer()
        messageTimer.schedule(object : TimerTask() {
            override fun run() {
                if (App.data.isInitialized) {
                    // enable buttons
                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        findViewById<Button>(R.id.btnStartGame).isEnabled = true
                        findViewById<Button>(R.id.btnPokedex).isEnabled = true
                    })

                    // update list of caught pokemon
                    val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
                    var caughtSet = sharedPreference.getStringSet("caught", mutableSetOf<String>())
                    if (caughtSet != null) {
                        App.data.refreshCaught(caughtSet)
                    }

                    // check if daily is already played
                    val dateSeed = LocalDateTime.now().dayOfYear + LocalDateTime.now().year
                    val dailySet = sharedPreference.getStringSet("dailies", mutableSetOf<String>())
                    if (dailySet != null && !dailySet.contains(dateSeed.toString())) {
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            findViewById<Button>(R.id.btnDaily).isEnabled = true
                        })
                    } else {
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            findViewById<TextView>(R.id.dailyDone).isVisible = true
                        })
                    }

                    this.cancel()
                }
            }
        }, 0, 250)



        // start Game activity on Start Game button tap
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // start Game activity on Start Daily button tap w/ daily param
        findViewById<Button>(R.id.btnDaily).setOnClickListener {
            val intent = Intent(this, Game::class.java)
            intent.putExtra("MODE", "daily");
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Start Pokedex activity on click of Pokedex button
        findViewById<Button>(R.id.btnPokedex).setOnClickListener {
            val intent = Intent(this, Pokedex::class.java)
            intent.putExtra("MODE", "random");
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

    override fun onResume() {
        super.onResume()

        if (App.data.isInitialized) {
            // enable buttons
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                findViewById<Button>(R.id.btnStartGame).isEnabled = true
                findViewById<Button>(R.id.btnPokedex).isEnabled = true
            })

            // update list of caught pokemon
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
            var caughtSet = sharedPreference.getStringSet("caught", mutableSetOf<String>())
            if (caughtSet != null) {
                App.data.refreshCaught(caughtSet)
            }

            // check if daily is already played
            val dateSeed = LocalDateTime.now().dayOfYear + LocalDateTime.now().year
            val dailySet = sharedPreference.getStringSet("dailies", mutableSetOf<String>())
            if (dailySet != null && !dailySet.contains(dateSeed.toString())) {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    findViewById<Button>(R.id.btnDaily).isEnabled = true
                })
            } else {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    findViewById<TextView>(R.id.dailyDone).isVisible = true
                    findViewById<Button>(R.id.btnDaily).isEnabled = false
                })
            }
        }
    }
}