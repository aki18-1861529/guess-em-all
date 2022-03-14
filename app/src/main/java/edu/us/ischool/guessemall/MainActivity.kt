package edu.us.ischool.guessemall

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

class NoConnectionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Internet connection is required to play.")
                .setPositiveButton("Exit",
                    DialogInterface.OnClickListener { dialog, id -> exitProcess(-1) })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class AirplaneModeDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Internet connection is required to play. Disable airplane mode?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
                    })
                .setNegativeButton("Exit",
                    DialogInterface.OnClickListener { dialog, id ->
                        exitProcess(-1)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class NoPermissionsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("App permissions must be enabled to play.")
                .setPositiveButton("Settings",
                    DialogInterface.OnClickListener { dialog, id ->
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    })
                .setNegativeButton("Exit",
                    DialogInterface.OnClickListener { dialog, id ->
                        exitProcess(-1)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class MainActivity : AppCompatActivity() {
    // creating variable for help menu dialog
    lateinit var helpDialog: Dialog

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // check connectivity
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isOnline(this)) {
                        App.data.lateInit()
                    } else {
                        if (Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {
                            val noConnectionAlert = NoConnectionDialogFragment()
                            noConnectionAlert.show(supportFragmentManager, "no_connection")
                        } else {
                            val airplaneModeAlert = AirplaneModeDialogFragment()
                            airplaneModeAlert.show(supportFragmentManager, "no_connection_airplane")
                        }
                    }
                } else {
                    val noConnectionAlert = NoPermissionsDialogFragment()
                    noConnectionAlert.show(supportFragmentManager, "no_permissions")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize help dialog
        helpDialog = Dialog(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
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

                    // check if daily is already played
                    val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
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
    }

    // handles all the code for the help menu dialog
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.status_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_help -> {
            // User chose the "Settings" item, show the app settings UI...
            openHelpDialog()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun openHelpDialog() : Unit {
        helpDialog.setContentView(R.layout.help_dialog)
        helpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var closeImg = helpDialog.findViewById<ImageView>(R.id.close_dialog)
        closeImg.setOnClickListener {
            helpDialog.dismiss()
        }
        helpDialog.show()
    }

}