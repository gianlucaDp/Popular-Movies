package gianlucadp.com.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Review implements Parcelable  {
    private String author;
    private String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content.trim();

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);

    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
