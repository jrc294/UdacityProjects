package com.learning.jonathan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the extra information from the intent and populate the detailActivity views
        Intent i = getActivity().getIntent();
        if (i != null) {
            if (i.hasExtra("MOVIE_TITLE")) {
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(i.getStringExtra("MOVIE_TITLE"));
            }
            if (i.hasExtra("RELEASE_DATE")) {
                ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(i.getStringExtra("RELEASE_DATE"));
            }
            if (i.hasExtra("VOTE_AVERAGE")) {
                ((TextView) rootView.findViewById(R.id.detail_average_rating)).setText(i.getStringExtra("VOTE_AVERAGE"));
            }
            if (i.hasExtra("OVERVIEW")) {
                ((TextView) rootView.findViewById(R.id.detail_overview)).setText(i.getStringExtra("OVERVIEW"));
            }
            if (i.hasExtra("POSTER_PATH")) {
                MainActivity.loadImage(getActivity(), (ImageView) rootView.findViewById(R.id.detail_image_poster), i.getStringExtra("POSTER_PATH"), "w342");
            }
        }
        return rootView;
    }
}
