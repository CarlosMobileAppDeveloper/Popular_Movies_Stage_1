package app.com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
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
public class PopularMoviesFragment extends Fragment {

    private static final String TAG = PopularMoviesFragment.class.getSimpleName();
    private GridView mMoviePostersGridView;
    private ProgressBar mProgressBar;
    private MoviePosterAdapter mMoviePosterAdapter;

    public PopularMoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mMoviePostersGridView = (GridView) rootView.findViewById(R.id.movie_poster_grid_view);
        mMoviePosterAdapter = new MoviePosterAdapter(this.getContext(), new ArrayList<Movie>());
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        // Instance of MoviePosterAdapter Class
        mMoviePostersGridView.setAdapter(mMoviePosterAdapter);

        // On Click event for Single Gridview Item
        mMoviePostersGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Pass Movie Object to the details class
                Intent movieDetailsIntent = new Intent(getContext(), MovieDetailsActivity.class);
                Movie movieDetails = (Movie) mMoviePosterAdapter.getItem(position);
                movieDetailsIntent.putExtra("movieDetails", movieDetails);
                startActivity(movieDetailsIntent);
                // Add animation for transition
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        loadPopularMovies();
    }

    private void loadPopularMovies()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        // Display a grid of movie posters based off sort type
        FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // NOTE: "movie_db_api_sort_by_popularity_desc" is the default value if the shared preference does not exist
        String sortType = sharedPrefs.getString(getString(R.string.pref_movie_sort_key), getString(R.string.movie_db_api_sort_by_popularity_desc));
        moviesTask.execute(sortType);
    }

    // TODO - Need to fix task to allow for paging
    public class FetchPopularMoviesTask extends AsyncTask<String, Void, Movie[]>
    {
        private final String TASK_LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();
        protected Movie[] doInBackground(String... params)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            Movie[] popularMoviesArray = null;

            try {
                String popularMoviesJsonStr;

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(getString(R.string.movie_db_api_url_scheme))
                        .authority(getString(R.string.movie_db_api_domain))
                        .appendPath(getString(R.string.movie_db_api_version))
                        .appendPath(getString(R.string.movie_db_api_action_type_discover))
                        .appendPath(getString(R.string.movie_db_api_search_type_movie))
                        .appendQueryParameter(getString(R.string.movie_db_api_query_parameter_sort), params[0])
                        .appendQueryParameter(getString(R.string.movie_db_api_query_parameter_api_key), BuildConfig.MOVIE_DB_API_KEY);
                URL url = new URL(builder.build().toString());

                // Create the request to Movies DB API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() != 0) {
                        popularMoviesJsonStr = buffer.toString();
                        popularMoviesArray = getPopularMovieDataFromJson(popularMoviesJsonStr);
                    }
                }

            } catch (IOException e) {
                Log.e(TASK_LOG_TAG, "Error ", e);
                // The code didn't successfully get the movie data
                popularMoviesArray = null;
            }
            catch (JSONException e)
            {
                Log.e(TASK_LOG_TAG, "JSON Data Parse Exception.");
                popularMoviesArray = null;
            }
            catch(Exception e)
            {
                Log.e(TASK_LOG_TAG, "Error ", e);
                popularMoviesArray = null;
            }
            finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TASK_LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return popularMoviesArray;
        }

        protected void onPostExecute(Movie[] moviesArray)
        {
            try
            {
                if(moviesArray != null)
                {
                    mMoviePosterAdapter.clear();
                    for(Movie movie :moviesArray)
                    {
                        mMoviePosterAdapter.add(movie);
                    }
                }
                else
                {
                    // Failed: Display Toast Message
                    Toast.makeText(getActivity(), getString(R.string.toast_message_loading_movies_failed), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                // Failed: Display Toast Message
                Toast.makeText(getActivity(), getString(R.string.toast_message_loading_movies_failed), Toast.LENGTH_SHORT).show();
            }
            finally {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    // TODO - Move into separate parser class
    private Movie[] getPopularMovieDataFromJson(String jsonResult) throws JSONException
    {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULT_LIST = "results";
        final String MDB_ID = "id";
        final String MDB_TITLE = "original_title";
        final String MDB_POSTER_IMAGE_PATH = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_AVERAGE_VOTE = "vote_average";
        final String MDB_VOTE_COUNT = "vote_count";
        final String MDB_GENRE_IDS = "genre_ids";

        JSONObject moviesJson = new JSONObject(jsonResult);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(MDB_RESULT_LIST);

        // Load results into movie array the results
        Movie[] moviesArray = new Movie[moviesJsonArray.length()];
        for(int index = 0; index < moviesJsonArray.length(); index++)
        {
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(index);
            JSONArray genreJsonArray = movieJsonObject.getJSONArray(MDB_GENRE_IDS);
            int[] genres = new int[genreJsonArray.length()];
            for(int genreIndex = 0; genreIndex < genreJsonArray.length(); genreIndex ++)
            {
                genres[genreIndex] = genreJsonArray.getInt(genreIndex);
            }
            Movie movieDetails = new Movie(movieJsonObject.getInt(MDB_ID),
                    movieJsonObject.getString(MDB_TITLE),
                    movieJsonObject.getString(MDB_POSTER_IMAGE_PATH),
                    movieJsonObject.getString(MDB_OVERVIEW),
                    movieJsonObject.getString(MDB_RELEASE_DATE),
                    movieJsonObject.getDouble(MDB_AVERAGE_VOTE),
                    movieJsonObject.getInt(MDB_VOTE_COUNT),
                    genres
            );
            moviesArray[index] = movieDetails;
        }
        return moviesArray;
    }
}
