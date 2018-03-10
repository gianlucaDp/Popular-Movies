package gianlucadp.com.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String originalTitle;
    private double popularity;
    private double vote_average;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String background;
    private byte[] posterImg;
    private byte[] backgroundImg;
    private int movieId;

    public Movie(String title, String overview, String releaseDate, String posterPath, double popularity, double vote_average, String background, int id) {
        this.originalTitle = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.background = background;
        this.movieId = id;
        this.posterImg = new byte[]{};
        this.backgroundImg = new byte[]{};
    }

    public Movie(String title, String overview, String releaseDate, double popularity, double vote_average, int id,byte[] posterImg, byte[] backgroundImg){
        this.originalTitle = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.movieId = id;
        this.posterImg = posterImg;
        this.backgroundImg = backgroundImg;
        this.posterPath="";
        this.background="";
    }

    public int getMovieId() {return movieId;}

    public void setMovieId(int movieId) {this.movieId = movieId;}

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public byte[] getPosterImg() {
        return posterImg;
    }

    public void setPosterImg(byte[] posterImg) {
        this.posterImg = posterImg;
    }

    public byte[] getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(byte[] backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeDouble(popularity);
        parcel.writeDouble(vote_average);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(background);
        parcel.writeInt(movieId);
        parcel.writeInt(posterImg.length);
        parcel.writeByteArray(posterImg);
        parcel.writeInt(backgroundImg.length);
        parcel.writeByteArray(backgroundImg);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        this.originalTitle = in.readString();
        this.popularity = in.readDouble();
        this.vote_average = in.readDouble();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.background = in.readString();
        this.movieId = in.readInt();
        this.posterImg = new byte[in.readInt()];
        in.readByteArray(this.posterImg);
        this.backgroundImg = new byte[in.readInt()];
        in.readByteArray(this.backgroundImg);

    }
}
