package cn.starrah.bluetoothvibrator2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.net.wifi.WifiManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.io.InputStreamReader
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

    val duration: Int
        get() = Integer.parseInt(txtDur.text.toString())
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

    class SendRequsetThread(private val ac : MainActivity) : Thread(){
        override fun run() {
            while (true) {
                sleep(500)
                if (ac.switch2.isChecked) {
                    val url = URL(ac.txtDur4.text.toString());
                    val urlConnection = url.openConnection() as HttpURLConnection
                    try {
                        //val bufferedInputStream = BufferedInputStream(urlConnection.inputStream)
                        val inputStreamReader = InputStreamReader(urlConnection.inputStream)
                        val charArray = CharArray(1)
                        inputStreamReader.read(charArray)
                        val res = charArray[0];
                        System.out.println("res"+res.toString())
                        if (res == '1') {
                            ac.vibrate()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        urlConnection.disconnect()
                    }
                }
            }
        }

    }
    val th = SendRequsetThread(this)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Start serve
        //Add server hosts
        //Set local IP
        //Initialize ArrayAdapter
        //Set fab click
        th.start()


        //Register media button event
        /*val filter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        registerReceiver(receiver, filter)*/
        //Set Media Session

//        mediaSession?.setPlaybackState(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        th.stop()
//        TinyWebServer.stopServer()
    }

    fun vibrate(){
        System.out.println("vibrate")
        val context = applicationContext
        val activity = this
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(activity.duration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
        else
            v.vibrate(activity.duration.toLong())
    }

    fun sendRequest(){
        System.out.println("sendRequest")
        val url = URL(txtDur4.text.toString());
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val bufferedInputStream = BufferedInputStream(urlConnection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection.disconnect()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        System.out.println("keycode"+keyCode)
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_MUTE -> {
                if(!switch2.isChecked) {
                    val asth = AsyncSignalThread(this)
                    asth.start()
                }
                button.requestFocus()
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    class AsyncSignalThread(private val ac : MainActivity) : Thread() {
        override fun run() {
            System.out.println("sendRequest")
            val url = URL(ac.txtDur4.text.toString());
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val bufferedInputStream = BufferedInputStream(urlConnection.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }
        }
    }
}
