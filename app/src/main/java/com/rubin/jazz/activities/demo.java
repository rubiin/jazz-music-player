package com.rubin.jazz.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.rubin.jazz.R;

import java.util.ArrayList;
import java.util.List;

public class demo extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard0 = new AhoyOnboarderCard("Welcome", "Lets go through a first time overview.", R.drawable.ic_empty_music2);
        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Customizable UI", "You can pimp the look and feel of the player.", R.drawable.ui);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Charts", "See the billboard top songs from within the player.", R.drawable.chartss);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Tag Editing", "Change the track info like artist, album, track name and so on", R.drawable.edit);
        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("Folder Browsing", "Browse folders to get the song of your choice", R.drawable.folder);
        AhoyOnboarderCard ahoyOnboarderCard5 = new AhoyOnboarderCard("Wallpaper", "Like the album art, set it as the wallpaper with this feature", R.drawable.art);
        AhoyOnboarderCard ahoyOnboarderCard6 = new AhoyOnboarderCard("Change Album art", "Change album art to a pic of your choice", R.drawable.artistinfoedit);


        ahoyOnboarderCard0.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard5.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard6.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();


        pages.add(ahoyOnboarderCard0);
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        pages.add(ahoyOnboarderCard5);
        pages.add(ahoyOnboarderCard6);


        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
            page.setTitleTextSize(dpToPixels(12, this));
            page.setDescriptionTextSize(dpToPixels(8, this));
            //page.setIconLayoutParams(width, height, marginTop, marginLeft, marginRight, marginBottom);
        }

        setFinishButtonTitle("Finish");
        showNavigationControls(true);
        setGradientBackground();

        //set the button style you created
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        }

        Typeface face = Typeface.createFromAsset(getAssets(), "materialdesignicons-webfont.ttf");
        setFont(face);

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        Toast.makeText(this, "Lets get started", Toast.LENGTH_SHORT).show();
        finish();

    }
}