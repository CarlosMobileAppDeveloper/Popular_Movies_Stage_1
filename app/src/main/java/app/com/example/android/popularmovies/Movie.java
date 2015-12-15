package app.com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carl on 14/12/2015.
 */
public class Movie implements Parcelable {
    int mId;
    String mTitle;
    String mPosterPath;
    String mOverview;
    String mReleaseDate;
    Double mAverageVote;
    int mVoteCount;
    int[] mGenresArray;

    // TODO - Load Genres via web service and store in DB
    private final Map<Integer,String> mMovieGenres = new HashMap<Integer, String>()
    {
        {
            put(28, "Action");
            put(12, "Adventure");
            put(16,"Animation");
            put(35,"Comedy");
            put(80,"Crime");
            put(99,"Documentary");
            put(18,"Drama");
            put(10751,"Family");
            put(14,"Fantasy");
            put(10769,"Foreign");
            put(36,"History");
            put(27,"Horror");
            put(10402,"Music");
            put(9648,"Mystery");
            put(10749,"Romance");
            put(878,"Science Fiction");
            put(10770,"TV Movie");
            put(53,"Thriller");
            put(10752,"War");
            put(37,"Western");
        }
    };

    // Constructor to init the Parcelable Movie Object
    public Movie(int id, String title, String posterPath, String overview, String releaseDate, Double averageVote, int voteCount, int[] genresArray)
    {
        this.mId = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mAverageVote = averageVote;
        this.mVoteCount = voteCount;
        this.mGenresArray = genresArray;
    }

    private Movie(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();
        this.mAverageVote = in.readDouble();
        this.mVoteCount = in.readInt();
        this.mGenresArray = in.createIntArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mAverageVote);
        dest.writeInt(mVoteCount);
        dest.writeIntArray(mGenresArray);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creator required for class implementing the parcelable interface
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getGenre(Integer genreId)
    {
        return mMovieGenres.get(genreId);
    }
}
