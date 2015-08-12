package com.learning.jonathan.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jonathan.Cook on 8/6/2015.
 */
public class MovieData implements Parcelable{
    private String posterPath;
    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private Bitmap posterW185;

    public MovieData(Parcel source) {
        setPosterPath(source.readString());
        setTitle(source.readString());
        setOverview(source.readString());
        setVoteAverage(source.readString());
        setReleaseDate(source.readString());
        Bitmap bmp185 = source.readParcelable(Bitmap.class.getClassLoader());
        setPosterW185(bmp185);
    }

    public MovieData() {}

    public String getOverview() {
        if (this.overview.equals("null")) {
            this.overview = "";
        }
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseDateOut() {
        if (releaseDate.length() == 10) {
            return releaseDate.substring(5, 7) + "/" + releaseDate.substring(8, 10) + "/" + releaseDate.substring(0, 4);
        } else {
            return "";
        }
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {

        @Override
        public MovieData createFromParcel(Parcel source) {
            return new MovieData(source);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public Bitmap getPosterW185() {
        return posterW185;
    }

    public void setPosterW185(Bitmap poster) {
        this.posterW185 = poster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPosterPath());
        dest.writeString(getTitle());
        dest.writeString(getOverview());
        dest.writeString(getVoteAverage());
        dest.writeString(getReleaseDate());
        dest.writeParcelable(getPosterW185(), 0);
    }
}
