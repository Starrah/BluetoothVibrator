package tech.rika.bluetoothviberator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL


class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("MediaButtonReceiver", "onReceive")
        if (Intent.ACTION_MEDIA_BUTTON != intent?.action) return
        val keyEvent = intent.extras[Intent.EXTRA_KEY_EVENT] as KeyEvent
        when (keyEvent.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_STOP,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY ->
                MainActivity.AsyncSignalThread().start()
        }
    }
}