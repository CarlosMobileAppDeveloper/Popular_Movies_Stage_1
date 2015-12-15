package app.com.example.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static app.com.example.android.popularmovies.R.layout;
import static app.com.example.android.popularmovies.R.string;

/**
 * Created by carl on 10/12/2015.
 * Description: Display a Grid of Movie Posters using Picasso
 */
public class MoviePosterAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Movie> mMovieList;

    // Constructor
    public MoviePosterAdapter(Context context, List<Movie> movieList){
        super(context, layout.fragment_popular_movies, movieList);
        mContext = context;
        mMovieList = movieList;
    }

    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView moviePosterImageView = new ImageView(mContext);
        if(mMovieList != null && mMovieList.size() > 0)
        {
            // Fixes image not displaying correctly
            moviePosterImageView.setScaleType(ImageView.ScaleType.FIT_START);
            moviePosterImageView.setAdjustViewBounds(true);
            // Build URL String for Movie Poster
            // TODO - Detect if Mobile or Tablet
            String url = mContext.getString(string.movie_poster_base_url) + mContext.getString(string.movie_poster_size_phone_default) + mMovieList.get(position).mPosterPath;
            // Load image using Picasso
            Picasso.with(mContext).load(url).into(moviePosterImageView);
        }

        return moviePosterImageView;
    }
}
