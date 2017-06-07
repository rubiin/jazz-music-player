/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.rubin.jazz.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rubin.jazz.MusicPlayer;
import com.rubin.jazz.R;
import com.rubin.jazz.utils.PreferencesUtility;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;

public class chart extends BaseThemedActivity  {

    String action;
   MediaScannerConnection scanner;
    EditText artist,album,year,track;
   Button bt,btart;
    String pathSong;
    AdView mAdView;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            setTheme(R.style.AppThemeNormalDark);
        else if (PreferencesUtility.getInstance(this).getTheme().equals("black"))
            setTheme(R.style.AppThemeNormalBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);


        // app id     ca-app-pub-7414148998580914~9658474785


      //  MobileAds.initialize(this, "ca-app-pub-7414148998580914~9658474785");

     /*   test add only
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
             .addTestDevice("9EE9AD96B8CCEDECDB00699D01940EE6")
                .build();
        mAdView.loadAd(adRequest);

*/

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Song Info");
        initialize();

        artist.setText(MusicPlayer.getArtistName());
        album.setText(MusicPlayer.getAlbumName());
        track.setText(MusicPlayer.getTrackName());
        year.setText(MusicPlayer.getReleaseDateForAlbum(getApplicationContext(), MusicPlayer.getCurrentAlbumId()));





        bt.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {

                Intent nb= new Intent(chart.this,lyrics.class);
                startActivity(nb);

            }
        });
        btart.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {

                Intent nb= new Intent(chart.this,AlbumArtActivity.class);
                startActivity(nb);

            }
        });

    }


    public void initialize(){

        artist=(EditText)findViewById(R.id.artisT);
        album=(EditText)findViewById(R.id.albuM);
        year=(EditText)findViewById(R.id.yeaR);

        track=(EditText)findViewById(R.id.sonG);
        bt=(Button)findViewById(R.id.buttoN);
        btart=(Button)findViewById(R.id.buttonArt);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        return true;
    }




public void editDetails(){

    try {
        pathSong=MusicPlayer.mService.getPath();
    } catch (RemoteException e) {
        e.printStackTrace();
    }


    File file=new File(pathSong);

        if (file != null)
            try {
                AudioFile af = AudioFileIO.read(file);
                TagOptionSingleton.getInstance().setAndroid(true);
                Tag tags = af.getTag();
                tags.setField(FieldKey.ARTIST, artist.getText().toString());
                tags.setField(FieldKey.YEAR, year.getText().toString());
                tags.setField(FieldKey.ALBUM,album.getText().toString());
                tags.setField(FieldKey.TITLE,track.getText().toString());

                af.setTag(tags);
                AudioFileIO.write(af);
                Snackbar.make(getWindow().getDecorView().getRootView(), "Track info updated",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
            } catch (CannotReadException | IOException | ReadOnlyFileException | TagException
                    | InvalidAudioFrameException | NullPointerException | CannotWriteException e) {
                e.printStackTrace();

            }
scanner =new MediaScannerConnection(getApplicationContext(),
            new MediaScannerConnection.MediaScannerConnectionClient() {

                public void onScanCompleted(String path, Uri uri) {
                    scanner.disconnect();
                }

                public void onMediaScannerConnected() {
                    scanner.scanFile(pathSong, "audio/*");
                }
            });

    scanner.connect();


    }




    public void Editdata(){
        try {
            pathSong=MusicPlayer.mService.getPath();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

            Toast.makeText(getApplicationContext(),""+pathSong ,Toast.LENGTH_LONG).show();


      File fileSong=new File(pathSong);

        AudioFile f = null;
        try {
            f = AudioFileIO.read(fileSong);
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        Tag tag = f.getTag();

        try {
            tag.setField(FieldKey.ARTIST,artist.getText().toString());

        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }



        try {
            f.commit();
            Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }


    }






    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_Edit:
            editDetails();
                return true;

            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

}




