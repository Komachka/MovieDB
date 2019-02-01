package com.example.katerynastorozh.themoviechooser;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofit;


    public static MovieDBService getRetrofitInstance()
    {
        if (retrofit == null)
        {
            retrofit =  new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(MovieDBService.class);

    }
}
