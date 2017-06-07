/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.rubin.jazz.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.dualcores.swagpoints.SwagPoints;
import com.rubin.jazz.ITimberService;
import com.rubin.jazz.MusicPlayer;
import com.rubin.jazz.MusicService;
import com.rubin.jazz.R;
import com.rubin.jazz.listeners.MusicStateListener;
import com.rubin.jazz.slidinguppanel.SlidingUpPanelLayout;
import com.rubin.jazz.subfragments.QuickControlsFragment;
import com.rubin.jazz.utils.Helpers;
import com.rubin.jazz.utils.NavigationUtils;
import com.rubin.jazz.utils.TimberUtils;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;



import static com.rubin.jazz.MusicPlayer.mService;

public class BaseActivity extends ATEActivity implements ServiceConnection, MusicStateListener {

    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;
    SwagPoints cs;
    int minutes;
    AlertDialog.Builder dialogBuilder;

    String pathSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToken = MusicPlayer.bindToService(this, this);

        mPlaybackStatus = new PlaybackStatus(this);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final IntentFilter filter = new IntentFilter();
        // Play and pause changes
        filter.addAction(MusicService.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicService.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(MusicService.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(MusicService.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public void onResume() {
        super.onResume();
        onMetaChanged();
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = ITimberService.Stub.asInterface(service);

        onMetaChanged();
    }


    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        mMusicStateListener.clear();
    }

    @Override
    public void onMetaChanged() {
        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void restartLoader() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!TimberUtils.hasEffectsPanel(BaseActivity.this)) {
            menu.removeItem(R.id.action_equalizer);
        }
        ATE.applyMenu(this, getATEKey(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;
            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.shuffleAll(BaseActivity.this);
                    }
                }, 80);

                return true;
            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
            case R.id.action_equalizer:
                NavigationUtils.navigateToEqualizer(this);
                return true;

          case R.id.action_sleep:
                AlertTimer();

           // set up timer
           ////////////////////////////////////////////////////////////////

                return true;


            case R.id.action_wallpaper:


            Wall();
                // set up timer
                ////////////////////////////////////////////////////////////////

                return true;


            case R.id.action_editSongInfo:
             //setup song info edit
                ///////////////////////////////////////////////////
                Intent fuck = new Intent(BaseActivity.this,chart.class);
                startActivity(fuck);

                return true;






            case R.id.action_share:

                Intent lyr = new Intent(BaseActivity.this,LyricsActivity.class);
                lyr.putExtra("mode","auto lyrics");
                startActivity(lyr);

  /*              try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Jazz");
                String sAux = "\nLet me recommend you this cool musicplayer application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=rubin.jazz \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }

            */

return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void AlertTimer(){

   dialogBuilder = new AlertDialog.Builder(this);

        // ...Irrelevant code for customizing the buttons and title

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView= inflater.inflate(R.layout.alert, null);

        cs= (SwagPoints)dialogView.findViewById(R.id.seekbar);
        cs.setOnSwagPointsChangeListener(new SwagPoints.OnSwagPointsChangeListener() {
            @Override
            public void onPointsChanged(SwagPoints swagPoints, int points, boolean fromUser) {

                minutes=points;
            }

            @Override
            public void onStartTrackingTouch(SwagPoints swagPoints) {

            }

            @Override
            public void onStopTrackingTouch(SwagPoints swagPoints) {

            }
        });

        dialogBuilder.setTitle("Sleep Timer");
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

/*
                    Intent intent = new Intent(BaseActivity.this, MyBroadcastReceiber.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(), 234324243, intent, 0);


                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + (minutes*60 * 1000), pendingIntent);
                            */
                Handler timeHandler = new Handler();
                timeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MusicPlayer.mService.pause();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Timer Reached",Toast.LENGTH_LONG).show();
                    }
                },minutes*60*1000 );

                Snackbar.make(getWindow().getDecorView().getRootView(), "Timer set as : "+minutes + " minutes",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();

            }

        });

        dialogBuilder.create().show();
    }


    public void Wall(){



        try {
            pathSong=MusicPlayer.mService.getPath();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //In place of "songpath" in the next line of code , give path of your audio file
        mmr.setDataSource(pathSong);
        byte [] data = mmr.getEmbeddedPicture();

        //coverart is an Imageview object
        // convert the byte array to a bitmap
        if(data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                myWallpaperManager.setBitmap(bitmap);
                Snackbar.make(getWindow().getDecorView().getRootView(), "Sucessfully set album Art as wall paper",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {

            Toast.makeText(getApplicationContext(),"No album art present..!!..!!",Toast.LENGTH_LONG).show();

        }




    }
    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }




    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;


        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MusicService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MusicService.PLAYSTATE_CHANGED)) {
//                    baseActivity.mPlayPauseProgressButton.getPlayPauseButton().updateState();
                } else if (action.equals(MusicService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MusicService.PLAYLIST_CHANGED)) {
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(MusicService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            QuickControlsFragment.topContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NavigationUtils.navigateToNowplaying(BaseActivity.this, false);
//                }
//            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
