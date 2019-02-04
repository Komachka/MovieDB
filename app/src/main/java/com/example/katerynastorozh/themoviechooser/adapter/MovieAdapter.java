package com.example.katerynastorozh.themoviechooser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.katerynastorozh.themoviechooser.R;
import com.example.katerynastorozh.themoviechooser.model.Movie;

import com.squareup.picasso.Picasso;
import com.example.katerynastorozh.themoviechooser.view.*;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<Movie> movies;


    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_list_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        movieViewHolder.movieRait.setText(movies.get(i).getVoteAverage().toString());
        movieViewHolder.movieTitle.setText(movies.get(i).getOriginalTitle());

        String imagePath = "https://image.tmdb.org/t/p/w500" + movies.get(i).getPosterPath();
        Picasso.get().load(imagePath).into(movieViewHolder.moviePic);



    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public TextView movieTitle, movieRait;
        public ImageView moviePic;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePic = itemView.findViewById(R.id.ivPic);
            movieTitle = itemView.findViewById(R.id.tvTitle);
            movieRait = itemView.findViewById(R.id.tvRaiting);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION)
                    {
                        Movie selectedMovie = movies.get(pos);
                        Intent intent = new Intent(context, MovieActivity.class);
                        intent.putExtra("movie", selectedMovie);
                        context.startActivity(intent);


                    }


                }
            });
        }
    }

}
