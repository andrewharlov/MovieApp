package com.harlov.playaround;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TheMovieDbAPI theMovieDbAPI;
    private RecyclerView recyclerViewMovies;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int nextPageToLoad;
    private int allPages;
    private String currentAction;
    private String searchQuery;
    private ArrayList<MovieItem> movieItemList;
    private ArrayList<MovieGenre> movieGenresList;
    MovieListAdapter movieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        recyclerViewMovies = (RecyclerView) findViewById(R.id.recycler_view_movies);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.moviesSwipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nextPageToLoad = 1;
                movieItemList.clear();
                movieListAdapter.notifyDataSetChanged();
                String apiKey = getResources().getString(R.string.api_key);
                if (movieGenresList.isEmpty()){
                    loadMovieGenres(apiKey);
                } else if (!movieGenresList.isEmpty()){
                    loadMovieItems(nextPageToLoad, apiKey);
                }
            }
        });

        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getAdapter().getItemCount() != 0){
                    if (!recyclerView.canScrollVertically(1)) {
                        String apiKey = getResources().getString(R.string.api_key);
                        if (nextPageToLoad <= allPages) {
                            if (currentAction.equals("discover")){
                                loadMovieItems(nextPageToLoad, apiKey);
                            } else if (currentAction.equals("search")){
                                loadSearchedMovies(searchQuery, nextPageToLoad, apiKey);
                            }
                        }
                    }
                }
            }
        });

        StaggeredGridLayoutManager staggeredGridManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewMovies.setLayoutManager(staggeredGridManager);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.api_end_point))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        theMovieDbAPI = retrofit.create(TheMovieDbAPI.class);

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("movieItemList")
                    && savedInstanceState.containsKey("nextPageToLoad")
                    && savedInstanceState.containsKey("allPages")
                    && savedInstanceState.containsKey("currentAction")
                    && savedInstanceState.containsKey("searchQuery")
                    && savedInstanceState.containsKey("movieGenresList")){
                nextPageToLoad = savedInstanceState.getInt("nextPageToLoad");
                allPages = savedInstanceState.getInt("allPages");
                movieItemList = savedInstanceState.getParcelableArrayList("movieItemList");
                currentAction = savedInstanceState.getString("currentAction");
                searchQuery = savedInstanceState.getString("searchQuery");
                movieGenresList = savedInstanceState.getParcelableArrayList("movieGenresList");

                movieListAdapter = new MovieListAdapter(movieItemList, movieGenresList, getBaseContext());
                recyclerViewMovies.setAdapter(movieListAdapter);
            }
        } else {
            nextPageToLoad = 1;
            movieItemList = new ArrayList<>();
            movieGenresList = new ArrayList<>();
            movieListAdapter = new MovieListAdapter(movieItemList, movieGenresList, getBaseContext());
            recyclerViewMovies.setAdapter(movieListAdapter);

            String apiKey = getResources().getString(R.string.api_key);
            loadMovieGenres(apiKey);
        }
    }

    void loadMovieGenres(String apiKey){
        Call<MovieGenreData> movieGenreDataCall = theMovieDbAPI.getGenres(apiKey);

        movieGenreDataCall.enqueue(new Callback<MovieGenreData>() {
            @Override
            public void onResponse(Call<MovieGenreData> call, Response<MovieGenreData> response) {
                MovieGenreData movieGenreData = response.body();
                movieGenresList.addAll(movieGenreData.getGenres());
                String apiKey = getResources().getString(R.string.api_key);
                loadMovieItems(nextPageToLoad, apiKey);
            }

            @Override
            public void onFailure(Call<MovieGenreData> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_msg, Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void loadMovieItems(int pageNumber, String apiKey){
        Call<MovieListData> movieListDataCall = theMovieDbAPI.getMovies(pageNumber, apiKey);

        movieListDataCall.enqueue(new Callback<MovieListData>() {
            @Override
            public void onResponse(Call<MovieListData> call, Response<MovieListData> response) {
                currentAction = "discover";

                MovieListData movieListData = response.body();
                movieItemList.addAll(movieListData.getResults());
                nextPageToLoad = movieListData.getPage() + 1;
                allPages = movieListData.getTotal_pages();

                movieListAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void onFailure(Call<MovieListData> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_msg, Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void loadSearchedMovies(String query, int pageNumber, String apiKey){
        Call<MovieListData> movieListDataCall = theMovieDbAPI.getSearchedMovies(query, pageNumber, apiKey);

        movieListDataCall.enqueue(new Callback<MovieListData>() {
            @Override
            public void onResponse(Call<MovieListData> call, Response<MovieListData> response) {
                currentAction = "search";

                MovieListData movieListData = response.body();
                movieItemList.addAll(movieListData.getResults());
                movieListAdapter.notifyDataSetChanged();

                if (nextPageToLoad < 2) recyclerViewMovies.smoothScrollToPosition(0);

                nextPageToLoad = movieListData.getPage() + 1;
                allPages = movieListData.getTotal_pages();
            }
            @Override
            public void onFailure(Call<MovieListData> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_msg, Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SearchView searchView = (SearchView) findViewById(R.id.search_view);
            searchView.clearFocus();

            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            nextPageToLoad = 1;
            movieItemList.clear();
            movieListAdapter.notifyDataSetChanged();
            String apiKey = getResources().getString(R.string.api_key);
            loadSearchedMovies(searchQuery, nextPageToLoad, apiKey);
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                }
            }
        });
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movieItemList", movieItemList);
        outState.putParcelableArrayList("movieGenresList", movieGenresList);
        outState.putInt("nextPageToLoad", nextPageToLoad);
        outState.putInt("allPages", allPages);
        outState.putString("currentAction", currentAction);
        outState.putString("searchQuery", searchQuery);
        super.onSaveInstanceState(outState);
    }
}
