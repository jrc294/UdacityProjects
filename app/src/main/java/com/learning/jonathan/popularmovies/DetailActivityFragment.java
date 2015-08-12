package com.learning.jonathan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_acivity, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the extra information from the intent and populate the detailActivity view
        Intent i = getActivity().getIntent();
        if (i != null) {
            MovieData movies = i.getExtras().getParcelable("MOVIE_DATA");
            if (movies != null) {
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movies.getTitle());
                ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movies.getReleaseDateOut());
                ((TextView) rootView.findViewById(R.id.detail_average_rating)).setText(movies.getVoteAverage() + "/10");
                ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movies.getOverview());
                ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image_poster);
                imageView.setImageBitmap(movies.getPosterW185());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                // Scale image up by 1.5 its normal size
                if (movies.getPosterW185() != null) {
                    ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                    lp.height = (movies.getPosterW185().getHeight() / 3) * 5;
                    lp.width = (movies.getPosterW185().getWidth() / 3) * 5;
                }
            }


        }
        return rootView;
    }
}
