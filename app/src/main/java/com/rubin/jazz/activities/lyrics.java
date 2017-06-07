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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class lyrics extends BaseThemedActivity {

     MediaScannerConnection scanner;
    EditText lyrics;
    String pathSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            setTheme(R.style.AppThemeNormalDark);
        else if (PreferencesUtility.getInstance(this).getTheme().equals("black"))
            setTheme(R.style.AppThemeNormalBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View/ Edit Song Lyrics");
        initialize();

        lyrics.setText(getLyrics(new File(pathSong)));


    }


    public void initialize() {

        lyrics = (EditText) findViewById(R.id.lyricS);
        try {
            pathSong=MusicPlayer.mService.getPath();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        return true;
    }


    public String getLyrics(File file) {
        String text;
        try {
            AudioFile af = AudioFileIO.read(file);
            TagOptionSingleton.getInstance().setAndroid(true);
            Tag tag = af.getTag();
            text = tag.getFirst(FieldKey.LYRICS);
            if (text.isEmpty())
                throw new NoSuchFieldException();
            //   text = text.replaceAll("\n", "<br/>");
        } catch (Exception e) {
            return null;
        }
        return text;
    }




    public void setLyrics(File file) {


        if (file != null)
            try {
                AudioFile af = AudioFileIO.read(file);
                TagOptionSingleton.getInstance().setAndroid(true);
                Tag tags = af.getTag();

                tags.setField(FieldKey.LYRICS, lyrics.getText().toString());
                af.setTag(tags);
                AudioFileIO.write(af);
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





        @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_Edit:
                setLyrics(new File(pathSong));
                Toast.makeText(getApplicationContext(),"Successful" ,Toast.LENGTH_LONG).show();
                return true;

            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

}




