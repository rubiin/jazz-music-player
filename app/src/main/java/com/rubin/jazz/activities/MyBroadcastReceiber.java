package com.rubin.jazz.activities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.RemoteException;
import android.widget.Toast;

import com.rubin.jazz.MusicPlayer;
import com.rubin.jazz.MusicService;

public class MyBroadcastReceiber extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context.getApplicationContext(),"Sleep Time finished, stopping music..!!",Toast.LENGTH_LONG).show();

        try {
            MusicPlayer.mService.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
}