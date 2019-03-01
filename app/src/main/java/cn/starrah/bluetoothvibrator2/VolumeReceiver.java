package cn.starrah.bluetoothvibrator2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class VolumeReceiver extends BroadcastReceiver {

        public MainActivity strUrl;
        public VolumeReceiver(MainActivity strUrl) {
            this.strUrl = strUrl;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("receive");
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                new Thread() {
                    public void run() {
                        System.out.println("sendRequest");
                        URL url = null;
                        try {
                            url = new URL(strUrl.getUrll());
                            URLConnection urlConnection = url.openConnection();
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

        }
    }
}
