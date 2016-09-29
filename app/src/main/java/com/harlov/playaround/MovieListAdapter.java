package com.harlov.playaround;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>{

    List<MovieItem> movieItems;
    List<MovieGenre> movieGenres;
    private final Context context;

    public MovieListAdapter(List<MovieItem> movieItems, List<MovieGenre> movieGenres, Context context){
        this.movieItems = movieItems;
        this.movieGenres = movieGenres;
        this.context = context;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.movielist_item, parent, false);
        return new MovieListViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        holder.movieTitle.setText(movieItems.get(position).getOriginal_title()
                + " (" + movieItems.get(position).getRelease_date().split("-", 2)[0] + ")");
        holder.movieTagline.setText(movieItems.get(position).getOverview());
        holder.movieVote.setText(Double.toString(movieItems.get(position).getVote_average()));

        String genres = "";
        ArrayList<Integer> genreIds = movieItems.get(position).getGenre_ids();
        Iterator<MovieGenre> iterator = movieGenres.iterator();
        while (iterator.hasNext()){
            MovieGenre item = iterator.next();
            if (genreIds.contains(item.getId())){
                genres = genres + item.getName() + " ";
            }
        }
        holder.movieGenres.setText(genres);

        Picasso.with(context).load("http://image.tmdb.org/t/p/w500" +
                movieItems.get(position).getBackdrop_path())
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return movieItems.size();
    }

    public static class MovieListViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView moviePoster;
        TextView movieTitle;
        TextView movieTagline;
        TextView movieVote;
        TextView movieGenres;

        public MovieListViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            moviePoster = (ImageView)itemView.findViewById(R.id.image_view_movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.text_view_movie_title);
            movieTagline = (TextView) itemView.findViewById(R.id.text_view_movie_tagline);
            movieVote = (TextView) itemView.findViewById(R.id.text_view_vote_average);
            movieGenres = (TextView) itemView.findViewById(R.id.text_view_movie_genres);
        }
    }
}
