package com.learning.jonathan.popularmovies;

/**
 * Created by Jonathan.Cook on 8/3/2015.
 */
public class Movie {
    private String posterPath;
    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public String getOverview() {
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage + "/10";
    }

    public String getReleaseDate() {
        return releaseDate.substring(5,7) + "/" + releaseDate.substring(8,10) + "/" + releaseDate.substring(0,4);
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
