package com.learning.jonathan.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MainActivity extends ActionBarActivity {

    public static final String API_KEY = "";

    // Static method used to load an image from a uri to a given image view
    static void loadImage(Context c, ImageView image_view, String poster_path, String width) {
        Uri posteruri = new Uri.Builder().scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(width)
                .appendPath(poster_path)
                .appendQueryParameter("api_key", API_KEY).build();

        Picasso.with(c).load(posteruri).into(image_view);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
