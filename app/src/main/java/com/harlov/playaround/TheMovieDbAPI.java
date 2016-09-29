package com.harlov.playaround;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDbAPI {
    @GET("discover/movie?&sort_by=popularity.desc")
    Call<MovieListData> getMovies(@Query("page") int pageNumber, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieListData> getSearchedMovies(@Query("query") String query, @Query("page")
        int pageNumber, @Query("api_key") String apiKey);

    @GET("genre/movie/list")
    Call<MovieGenreData> getGenres(@Query("api_key") String apiKey);
}

