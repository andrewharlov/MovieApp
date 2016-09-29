package com.harlov.playaround;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieGenreData {
    @SerializedName("genres")
    @Expose
    private ArrayList<MovieGenre> genres;

    public ArrayList<MovieGenre> getGenres() {
        return genres;
    }
}
