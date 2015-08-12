package com.learning.jonathan.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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

import com.squareup.picasso.Picasso;

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
    private boolean restartedFromChildActivity = false;

    public static final String API_KEY = "";
    private static final String SORT_ORDER_POPULAR = "popularity.desc";
    private static final String SORT_ORDER_RATED = "vote_average.desc";

    // Movie array stores the details of all movies loaded from the json return value

    MovieStore m_movies_pop = new MovieStore(SORT_ORDER_POPULAR);
    MovieStore m_movies_rated = new MovieStore(SORT_ORDER_RATED);

    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            m_gridView.setNumColumns(4);
        } else {
            m_gridView.setNumColumns(2);
        }
        // Only refresh the grid if the activity was not started as a result of coming back from the detailed activity
        if (!restartedFromChildActivity) {
            loadMovieCache();
        }
        restartedFromChildActivity = false;
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

        //1. Retrieve the MovieStores from the cache
        if (savedInstanceState != null) {
            m_movies_pop = savedInstanceState.getParcelable("movies_pop");
            m_movies_rated  = savedInstanceState.getParcelable("movies_rated");
        }

        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When the imageView is clicked, add some extras which trhe detail view will use to populate its views and start the activity
                Intent i = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                MovieData movieData;
                if (getCurrentSortOrder().equals("popular")) {
                    movieData = m_movies_pop.getMovieData().get(position);
                } else {
                    movieData = m_movies_rated.getMovieData().get(position);
                }
                bundle.putParcelable("MOVIE_DATA", movieData);
                i.putExtras(bundle);
                startActivityForResult(i, 0);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Set a flag to indicate if the detail activity has just closed
        restartedFromChildActivity = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the movie stores
        outState.putParcelable("movies_pop", m_movies_pop);
        outState.putParcelable("movies_rated", m_movies_rated);
    }

    private void loadMovieCache() {
        // Load move cache will load up two MovieStore objects, representing popular movies and highly rated movies.
        // If we cache these on create, then no further internet access will be required until the activity is destroyed.

        // 1. If movies_pop is empty or movie_rated is empty, then load 'em up.
        if (m_movies_pop.getMovieData().size() == 0) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(m_movies_pop);
        }

        if (m_movies_rated.getMovieData().size() == 0) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(m_movies_rated);
        }

        if ((m_movies_pop.getMovieData().size() == 0) || (m_movies_rated.getMovieData().size() == 0)) {
            Toast.makeText(getActivity(), R.string.data_refresh, Toast.LENGTH_SHORT).show();
        }

        // 2. If the movies have not been loaded by the async task, I need to pass a MovieStore to the array adapter, and use the appropraie one depending on the
        // sort order.
        if (getCurrentSortOrder().equals("popular") && (m_movies_pop.getMovieData().size() > 0)) {
            m_gridView.setAdapter(new ImageAdapter(getActivity(), m_movies_pop));
        } else if (getCurrentSortOrder().equals("highestRated") && (m_movies_rated.getMovieData().size() > 0)) {
            m_gridView.setAdapter(new ImageAdapter(getActivity(), m_movies_rated));
        }
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<MovieData> mMovies;

        public ImageAdapter(Context c, MovieStore movies) {
            mContext = c;
            mMovies = movies.getMovieData();
        }

        @Override
        public int getCount() {
            return mMovies.size();
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
            imageView.setImageBitmap(mMovies.get(position).getPosterW185());

            return imageView;

        }
    }

    public class FetchMoviesTask extends AsyncTask<MovieStore, Void, MovieStore> {

        // Async task to retrieve the movie details from the movie database and receive a json string which is parsed and loaded into an ArrayList of Movie classes

        @Override
        protected void onPostExecute(MovieStore movieStore) {

            // 1. If the movieStore is for the current sort order then load it into the image adapter
            if ((getCurrentSortOrder().equals("popular") && movieStore.getSortOrder().equals(SORT_ORDER_POPULAR)) || (getCurrentSortOrder().equals("highestRated") && movieStore.getSortOrder().equals(SORT_ORDER_RATED))  && movieStore.getMovieData().size() != 0) {
                m_gridView.setAdapter(new ImageAdapter(getActivity(), movieStore));
            }
            if (movieStore.getMovieData().size() == 0) {
                Toast.makeText(getActivity(), getResources().getText(R.string.failed_to_load), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected MovieStore doInBackground(MovieStore... params) {

            MovieStore movieStore = params[0];

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
                        .appendQueryParameter("sort_by", movieStore.getSortOrder())
                        .appendQueryParameter("api_key", API_KEY).build().toString());
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
                    movieStore.setMovieData(getMovieDataFromJson(moviesJsonStrArray));
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

            return movieStore;
        }


        private ArrayList<MovieData> getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            // Parse thru the json string and load into a collection of Movie data

            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_TITLE = "original_title";
            final String MDB_RESULTS = "results";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_AVERAGE_RATING = "vote_average";
            final String MDB_OVERVIEW = "overview";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(MDB_RESULTS);

            ArrayList<MovieData> results = new ArrayList<>();
            Bitmap bmp185 = null;
            Bitmap bmp342 = null;

            for (int i = 0; i < resultsArray.length(); i++) {
                MovieData movie = new MovieData();
                JSONObject movieObject = resultsArray.getJSONObject(i);
                movie.setPosterPath(movieObject.getString(MDB_POSTER_PATH).replace("/", ""));
                movie.setTitle(movieObject.getString(MDB_TITLE));
                movie.setReleaseDate(movieObject.getString(MDB_RELEASE_DATE));
                movie.setVoteAverage(movieObject.getString(MDB_AVERAGE_RATING));
                movie.setOverview(movieObject.getString(MDB_OVERVIEW));


                Uri posteruri185 = new Uri.Builder().scheme("http")
                        .authority("image.tmdb.org")
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185")
                        .appendPath(movie.getPosterPath())
                        .appendQueryParameter("api_key", API_KEY).build();
                try {
                    bmp185 = Picasso.with(getActivity()).load(posteruri185).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                movie.setPosterW185(bmp185);

                results.add(movie);
            }
            return results;
        }
    }

    private String getCurrentSortOrder() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));
    }

}
