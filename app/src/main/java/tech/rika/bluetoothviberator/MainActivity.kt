package tech.rika.bluetoothviberator

import android.app.PendingIntent
import android.content.Intent
import android.media.session.MediaSession
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        val hosts: MutableList<String> = ArrayList()
    }

    private val receiver = tech.rika.bluetoothviberator.MediaButtonReceiver()
    val duration: Int
        get() = Integer.parseInt(txtDur.text.toString())
    private var server: HttpdWebServer? = null
    private val localIP: String
        get() {
            return try {
                val wm: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val wifiinfo = wm.connectionInfo
                val myIPAddress = BigInteger.valueOf(wifiinfo.ipAddress.toLong()).toByteArray()
                myIPAddress.reverse()
                val myInetIP = InetAddress.getByAddress(myIPAddress)
                myInetIP.hostAddress
            } catch (e: Exception) {
                "(Error when accessing WiFi status.)"
            }
        }

//    private var adapter: ArrayAdapter<String>? = null

    private var mediaSession: MediaSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Start server
        server = HttpdWebServer(8080, applicationContext, this)
        server?.start()
        //Add server hosts
        if (!hosts.contains("localhost:8080")) hosts.add("localhost:8080")
        //Set local IP
        txtIP.setText(localIP)
        //Initialize ArrayAdapter
        lsv.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, hosts)
        //Set fab click
        fabAdd.setOnClickListener {
            run {
                val input = EditText(applicationContext)
                AlertDialog.Builder(this).setTitle("Add new server")
                    .setView(input)
                    .setPositiveButton("Add") { _, _ ->
                        hosts.add(
                            if (input.text.toString().indexOf(':') > 0)
                                input.text.toString()
                            else input.text.toString() + ":8080"
                        )
                    }
                    .setNegativeButton("Cancel") { _, _ -> Unit }
                    .show()
            }
        }
        //Register media button event
        /*val filter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        registerReceiver(receiver, filter)*/
        //Set Media Session
        val intent = Intent(applicationContext, MediaButtonReceiver::class.java)
        mediaSession = MediaSession(this, "Vibrator")
        mediaSession?.setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, intent, 0))
        mediaSession?.isActive=true
//        mediaSession?.setPlaybackState(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
//        TinyWebServer.stopServer()
        server?.stop()
        unregisterReceiver(receiver)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_MUTE ->
                AsyncSignalThread().start()
        }

        return super.onKeyDown(keyCode, event)
    }

    class AsyncSignalThread : Thread() {
        override fun run() {
//            activity =
            for (host in hosts) {
                val url = URL("http://$host/signal")
                val urlConnection = url.openConnection() as HttpURLConnection
                try {
                    BufferedInputStream(urlConnection.inputStream)
                } catch (e: Exception) {
                } finally {
                    urlConnection.disconnect()
                }
            }
        }
    }

}


