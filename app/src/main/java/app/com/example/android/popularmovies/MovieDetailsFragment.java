package app.com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        // Fetch the Movie details from the intent
        Intent intent = getActivity().getIntent();
        Movie movieDetails = intent.getParcelableExtra("movieDetails");

        if(movieDetails != null)
        {
            // Display Movie details and load movie poster using Picasso
            TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_title_text_view);
            titleTextView.setText(movieDetails.mTitle);

            TextView genresTextView = (TextView) rootView.findViewById(R.id.movie_genres_text_view);
            String genreDisplayText = "";
            for(int index = 0; index < movieDetails.mGenresArray.length; index++)
            {
                genreDisplayText = genreDisplayText + movieDetails.getGenre((Integer) movieDetails.mGenresArray[index]);
                if(index+1 < movieDetails.mGenresArray.length)
                    genreDisplayText = genreDisplayText + " | ";
            }
            genresTextView.setText(genreDisplayText);

            // TODO - movie_running_time_text_view - No running time in API?

            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.movie_synopsis_text_view);
            synopsisTextView.setText(movieDetails.mOverview);

            ImageView moviePosterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_image_view);
            // Build URL String for Movie Poster
            // TODO - Detect if Mobile or Tablet
            String url = getString(R.string.movie_poster_base_url) + getString(R.string.movie_poster_size_phone_default) + movieDetails.mPosterPath;
            // Load image using Picasso
            Picasso.with(this.getActivity()).load(url).into(moviePosterImageView);

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_text_view);

            try{
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar releaseDate = Calendar.getInstance();
                releaseDate.setTime(dateformat.parse(movieDetails.mReleaseDate));
                releaseDateTextView.setText(Integer.toString(releaseDate.get(Calendar.YEAR)));
            }
            catch (Exception e)
            {
                Log.e(TAG, "Date parse exception: " + e.toString());
            }


            TextView averageVoteTextView = (TextView) rootView.findViewById(R.id.average_rating_text_view);
            averageVoteTextView.setText(movieDetails.mAverageVote.toString());

            TextView totalVotesTextView = (TextView) rootView.findViewById(R.id.total_votes_text_view);
            totalVotesTextView.setText(movieDetails.mVoteCount + " votes");

        }

        return rootView;
    }


}
