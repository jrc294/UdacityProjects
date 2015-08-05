package com.learning.jonathan.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GridView m_gridView = null;

    // currentSortOrder variable is used to hold the previous sort order when populating the grid. Grid will only populate if sort order changes.
    private String currentSortOrder = "";

    // Movie array stores the details of all movies loaded from the json return value
    ArrayList<Movie> m_movies;

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Reload the gridview if the activity starts. This will occur on screen orienatation changes as well
        updateMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Start the settings activity when the menu option is chosen
        if (id == R.id.settings_action) {
            Intent i = new Intent(getActivity(), SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        m_gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);

        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When the imageView is clicked, add some extras which trhe detail view will use to populate its views and start the activity
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("MOVIE_TITLE",m_movies.get(position).getTitle());
                i.putExtra("POSTER_PATH", m_movies.get(position).getPosterPath());
                i.putExtra("RELEASE_DATE", m_movies.get(position).getReleaseDate());
                i.putExtra("VOTE_AVERAGE", getResources().getString(R.string.user_rating) + " " + m_movies.get(position).getVoteAverage());
                i.putExtra("OVERVIEW", m_movies.get(position).getOverview());
                startActivity(i);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        // This method will be run when the activity starts. The grid view will be populated if teh sort order has changed since the last time it was run.
        // If the sort order has not changed the view will remain positioned from where it was left off. This is better for when a user moves to the detail view
        // and then switches back. If the user switches from portrait to landscape, the view gridview will repopulate however. Just have to live with that.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = settings.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));
        // Only update the movies if sort order has changed. I don't like the fact that the movie image reverts back to the top after coming back from the detail activity
        if ((sortOrder == null) || !sortOrder.equals(currentSortOrder)) {
            currentSortOrder = sortOrder;
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(sortOrder);
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Movie> mPosterPaths;

        public ImageAdapter(Context c, ArrayList<Movie> posterPaths) {
            mContext = c;
            mPosterPaths = posterPaths;
        }

        @Override
        public int getCount() {
            return mPosterPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        // getView will create the imageViews for the gridWview
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setAdjustViewBounds(true);

            // Make a call to the static method of the MainActivity to load the image using Picasso. The static method is shared by the detailActivity to load its poster.
            MainActivity.loadImage(mContext, imageView, mPosterPaths.get(position).getPosterPath(), "w185");

            return imageView;

        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        // Async task to retrieve the movie details from the movie database and receive a json string which is parsed and loaded into an ArrayList of Movie classes

        @Override
        protected void onPostExecute(ArrayList<Movie> resultsArray) {
            // We want to refresh the image adapter
            if (resultsArray != null) {
                // Load the poster files into a String[] array for the adapter
                m_gridView.setAdapter(new ImageAdapter(getActivity(),resultsArray));
            } else {
                Toast.makeText(getActivity(), getResources().getText(R.string.failed_to_load), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            final String SORT_ORDER_MOST_POPULAR = "popular";
            final String SORT_ORDER_MOST_POPULAR_PARAM = "popularity.desc";
            final String SORT_ORDER_HIGHEST_RATED_PARAM = "vote_average.desc";

            String sortOrder = params[0];
            String sortOrderParam;
            if (sortOrder.equals(SORT_ORDER_MOST_POPULAR)) {
                sortOrderParam = SORT_ORDER_MOST_POPULAR_PARAM;
            } else {
                sortOrderParam = SORT_ORDER_HIGHEST_RATED_PARAM;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStrArray;

            try {
                // Construct the URL for the themoviedb query
                URL url = new URL(new Uri.Builder().scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by",sortOrderParam)
                        .appendQueryParameter("api_key",MainActivity.API_KEY).build().toString());
                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStrArray = null;
                }
                moviesJsonStrArray = buffer.toString();

                try {
                    m_movies = getMovieDataFromJson(moviesJsonStrArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return m_movies;
        }


        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            // Parse thru the json string and load into a collection of Movie data

            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_TITLE = "original_title";
            final String MDB_RESULTS = "results";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_AVERAGE_RATING = "vote_average";
            final String MDB_OVERVIEW = "overview";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(MDB_RESULTS);

            ArrayList<Movie> results = new ArrayList<>();

            for (int i = 0; i < resultsArray.length(); i++) {
                Movie movie = new Movie();
                JSONObject movieObject = resultsArray.getJSONObject(i);
                movie.setPosterPath(movieObject.getString(MDB_POSTER_PATH).replace("/", ""));
                movie.setTitle(movieObject.getString(MDB_TITLE));
                movie.setReleaseDate(movieObject.getString(MDB_RELEASE_DATE));
                movie.setVoteAverage(movieObject.getString(MDB_AVERAGE_RATING));
                movie.setOverview(movieObject.getString(MDB_OVERVIEW));
                results.add(movie);
            }
            return results;
        }
    }

}
