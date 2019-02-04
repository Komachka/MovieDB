package com.example.katerynastorozh.themoviechooser;

import com.example.katerynastorozh.themoviechooser.model.MovieResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDBService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Observable<MovieResponse> getPopularMoviesRX(@Query("api_key") String apiKey);
}
