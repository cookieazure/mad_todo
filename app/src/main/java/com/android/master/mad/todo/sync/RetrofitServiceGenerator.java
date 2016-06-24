package com.android.master.mad.todo.sync;

import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MISSLERT on 04.06.2016.
 * Class for generating Retrofit services using defined interfaces.
 */
public class RetrofitServiceGenerator {

    private static final String LOG_TAG = RetrofitServiceGenerator.class.getSimpleName();

    public static final String API_BASE_URL = "http://10.0.2.2:8080/api/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Log.v(LOG_TAG, " : createService().");
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
