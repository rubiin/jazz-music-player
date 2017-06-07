package com.rubin.jazz.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rubin.jazz.MusicPlayer;
import com.rubin.jazz.R;
import com.rubin.jazz.utils.PreferencesUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LyricsActivity extends ActionBarActivity {

    EditText artist,song, lyric;
    Button bt;
    StringBuilder tmp ;

    String artistName,songName;
    ///for testing use these url
    String urlS="http://www.google.com",res,red="http://www.azlyrics.com/lyrics/eminem/lovethewayyoulie.html";

    // main url


    public void StartLyrics() throws IOException {

        URL url = null;
        try {
            url = new URL(red);

        HttpURLConnection urlConn = null;

            urlConn = (HttpURLConnection) url.openConnection();

            String line = null;
        tmp = new StringBuilder();
        BufferedReader in = null;

            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            while ((line = in.readLine()) != null) {
                tmp.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Toast.makeText(getApplicationContext(),tmp,Toast.LENGTH_LONG).show();
        lyric.setText(tmp);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            setTheme(R.style.AppThemeNormalDark);
        else if (PreferencesUtility.getInstance(this).getTheme().equals("black"))
            setTheme(R.style.AppThemeNormalBlack);
        setContentView(R.layout.activity_lyrics2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Get Lyrics");

      //  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

      //  StrictMode.setThreadPolicy(policy);


        artist=(EditText)findViewById(R.id.artisTs);
        lyric=(EditText)findViewById(R.id.Lyrics);


        song=(EditText)findViewById(R.id.sonGs);
        bt=(Button)findViewById(R.id.bt);

        Intent mode= getIntent();
        if(mode.getStringExtra("mode").equalsIgnoreCase("auto lyrics")){

            if(MusicPlayer.getArtistName().equalsIgnoreCase("<unknown>")) {
                artistName=MusicPlayer.getTrackName().split("-")[0].toLowerCase().replace(" ", "").trim();
                songName=MusicPlayer.getTrackName().split("-")[1].toLowerCase().replace(" ", "").trim();
                artist.setText(artistName);
                song.setText(songName);
                LyricsHandler();
            }


            else{
                artistName= MusicPlayer.getArtistName().toLowerCase().replace(" ", "").trim();

                songName=MusicPlayer.getTrackName().toLowerCase().replace(" ", "").trim();
                artist.setText(artistName);
                song.setText(songName);
                LyricsHandler();

            }


        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artistName=artist.getText().toString().toLowerCase().replace(" ", "").trim();
                 songName=song.getText().toString().toLowerCase().replace(" ", "").trim();

                LyricsHandler();


            }
        });





    }



    public void LyricsHandler(){

        String urlData="http://www.azlyrics.com/lyrics/"+artistName+"/"+songName+".html";

        Log.d("URLDATA ",urlData);
        Toast.makeText(getApplicationContext(),urlData,Toast.LENGTH_LONG).show();
        try {
            res=new MyTask().execute(urlData).get();
            Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
            lyric.setText(res);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }




    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  loading = ProgressDialog.show(getApplicationContext(),"Fetching ","Meanwhile please take a cup of coffee...",false,false);

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            //String urlData = params[0];
            StringBuilder response = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36");
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);


                BufferedReader in = new BufferedReader(new InputStreamReader(
            urlConnection.getInputStream()));
    String inputLine;


    while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
    }
    in.close();

}

catch(IOException e){

    e.printStackTrace();
}


            // lyrics is stored between these two strings
            String up_boundary="<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->";
            String lower_boundary="<!-- MxM banner -->";
           String data=response.toString();
            data=data.split(up_boundary)[1];
            data=data.split(lower_boundary)[0];
            data=data.replace("<br>", "\n").replace("</div>", "").replace("</br>", " ").replace("<i>", " ").replace("</i>", "\n").trim();



            return data;

     //      return response.toString();


        }



        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //    p.dismiss();

            // Do things like hide the progress bar or change a TextView
        }

    }



    @Override
    protected void onPause() {
        super.onPause();

    }














}








