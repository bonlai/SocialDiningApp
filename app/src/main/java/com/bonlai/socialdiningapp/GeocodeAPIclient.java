package com.bonlai.socialdiningapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Bon Lai on 10/2/2018.
 */

public class GeocodeAPIclient {
    static Retrofit mRetrofit;
    static APIService mAPIService;

    public static Retrofit retrofit() {
        if (mRetrofit == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return mRetrofit;
    }

    public static APIService getAPIService() {
        mAPIService=GeocodeAPIclient.retrofit().create(APIService.class);
        return mAPIService;
    }


    public interface APIService {
        @GET("geocode/json")
        Call<ResponseBody> getCityResults(@Query("address") String types, @Query("key") String key);
    }
}
