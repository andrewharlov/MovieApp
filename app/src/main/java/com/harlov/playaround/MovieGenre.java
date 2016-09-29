package com.harlov.playaround;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieGenre implements Parcelable{
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;


    protected MovieGenre(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<MovieGenre> CREATOR = new Creator<MovieGenre>() {
        @Override
        public MovieGenre createFromParcel(Parcel in) {
            return new MovieGenre(in);
        }

        @Override
        public MovieGenre[] newArray(int size) {
            return new MovieGenre[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
