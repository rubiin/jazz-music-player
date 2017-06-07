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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rubin.jazz.MusicPlayer;
import com.rubin.jazz.R;
import com.rubin.jazz.utils.PreferencesUtility;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AlbumArtActivity extends BaseThemedActivity {

    MediaScannerConnection scanner;
    ImageView albumArt;
    String pathSong;
    File file,myImageFile;
    final int SELECT_PHOTO = 1;
    AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            setTheme(R.style.AppThemeNormalDark);
        else if (PreferencesUtility.getInstance(this).getTheme().equals("black"))
            setTheme(R.style.AppThemeNormalBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_art);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View/ Edit Song Lyrics");
        initialize();

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
            }
        });




    }


    public void UpdateAlbumArt(){


        try {
            pathSong=MusicPlayer.mService.getPath();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        file = new File(pathSong);



        try {
            TagOptionSingleton.getInstance().setAndroid(true);
            AudioFile f = AudioFileIO.read(file);
            f.setTag(new ID3v23Tag());
            Tag tag = f.getTag();
            Artwork a = ArtworkFactory.createArtworkFromFile(myImageFile);
            tag.deleteArtworkField();
            tag.setField(a);
            f.commit();
            myImageFile=null;
        } catch (Exception e) {
            Log.i("ArtWork Failed:", e.toString());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        Log.i("imageUri: ", imageUri.toString());
                        Log.i("real path", getRealPathFromURI(getApplicationContext(),imageUri));

                        File myImageFile=new File(getRealPathFromURI(getApplicationContext(),imageUri));

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Log.i("imagestream", imageStream.toString());
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        albumArt.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }





    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public void initialize() {

        albumArt = (ImageView) findViewById(R.id.albumArtS);
      //  btart=(Button)findViewById(R.id.buttonArt2);
     myImageFile=null;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        return true;
    }






    public void updateMedia() {

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

                // add image to song
               UpdateAlbumArt();
                updateMedia();
                Toast.makeText(getApplicationContext(),"Successful" ,Toast.LENGTH_LONG).show();
                return true;

            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

}




