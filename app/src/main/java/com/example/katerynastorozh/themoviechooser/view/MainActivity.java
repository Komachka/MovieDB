package com.example.katerynastorozh.themoviechooser.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.katerynastorozh.themoviechooser.MovieDBService;
import com.example.katerynastorozh.themoviechooser.R;
import com.example.katerynastorozh.themoviechooser.RetrofitInstance;
import com.example.katerynastorozh.themoviechooser.adapter.MovieAdapter;
import com.example.katerynastorozh.themoviechooser.model.Movie;
import com.example.katerynastorozh.themoviechooser.model.MovieResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayList<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Call<MovieResponse> resp;
    private Observable<MovieResponse> movieResponseObservable;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPopularMoviesRX();


        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPopularMoviesRX();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView = findViewById(R.id.recycler);
        movieAdapter = new MovieAdapter(this, movies);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setAdapter(movieAdapter);


    }

    private void getPopularMovies() {
        MovieDBService service = RetrofitInstance.getRetrofitInstance();
        Call<MovieResponse> resp = service.getPopularMovies(getString(R.string.api_key));


        resp.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse movieResponse = response.body();
                Log.i(LOG_TAG, response.message());

                if (movieResponse != null && movieResponse.getMovies() != null)
                {
                    movies = (ArrayList<Movie>)movieResponse.getMovies();
                    showOnRecyclerView();
                }

            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });
    }


    private void getPopularMoviesRX() {
        MovieDBService service = RetrofitInstance.getRetrofitInstance();
        movieResponseObservable = service.getPopularMoviesRX(getString(R.string.api_key));

        DisposableObserver observer = movieResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<MovieResponse, Observable<Movie>>() {
                    @Override
                    public Observable<Movie> apply(MovieResponse movieResponse) throws Exception {
                        return Observable.fromArray(movieResponse.getMovies().toArray(new Movie[0]));
                    }
                })
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(Movie movie) throws Exception {
                        return movie.getVoteAverage() > 7.0;
                    }
                })
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(Movie movie) throws Exception {

                       return  !getMoviesIdsArray().contains(movie.getId());
                    }
                })
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        movies.add(movie);


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        showOnRecyclerView();
                    }
                });
        compositeDisposable.add(observer);


    }




    private void showOnRecyclerView() {

        movieAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    private ArrayList<Integer> getMoviesIdsArray()
    {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Movie m : movies)
        {
            ids.add(m.getId());
        }
        return  ids;
    }
}
