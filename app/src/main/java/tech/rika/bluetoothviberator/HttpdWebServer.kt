package tech.rika.bluetoothviberator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ActionMenuView
import android.widget.Toast
import fi.iki.elonen.NanoHTTPD
import java.net.URI

class HttpdWebServer(port: Int, private val context: Context, private val activity: MainActivity) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession?): Response {
        if (URI(session?.uri).path == "/signal") {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                v.vibrate(VibrationEffect.createOneShot(activity.duration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            else
                v.vibrate(activity.duration.toLong())
            Toast.makeText(context, "Vibrate signal received", Toast.LENGTH_LONG)
                .show()
        }
        return newFixedLengthResponse("")
    }
}

