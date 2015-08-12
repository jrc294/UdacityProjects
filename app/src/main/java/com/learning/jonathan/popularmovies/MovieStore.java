package com.learning.jonathan.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jonathan.Cook on 8/6/2015.
 */
public class MovieStore implements Parcelable {

    public ArrayList<MovieData> movieData = new ArrayList<MovieData>();
    public ArrayList<MovieData> getMovieData() {
        return movieData;
    }

    public void setMovieData(ArrayList<MovieData> movieData) {
        this.movieData = movieData;
    }

    private String m_sortOrder;

    MovieStore(String sortOrder) {
        this.m_sortOrder = sortOrder;
        movieData = new ArrayList<MovieData>();
    }

    public MovieStore(Parcel in) {
        in.readTypedList(getMovieData(), MovieData.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(movieData);
    }


    public static final Parcelable.Creator<MovieStore> CREATOR = new Parcelable.Creator<MovieStore>() {
        @Override
        public MovieStore createFromParcel(Parcel in) {
            return new MovieStore(in);
        }

        @Override
        public MovieStore[] newArray(int size) {
            return new MovieStore[size];
        }
    };

    public String getSortOrder() {
        return m_sortOrder;
    }

    public void setSortOrder(String m_sortOrder) {
        this.m_sortOrder = m_sortOrder;
    }
}
