package com.bonlai.socialdiningapp;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Bon Lai on 12/1/2018.
 */

public class APIclient {
    //static Retrofit mRetrofit;
    static Retrofit mRetrofit;
    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            mRetrofit = new Retrofit.Builder().
                    baseUrl("http://192.168.2.5:8000/").
                    addConverterFactory(GsonConverterFactory.create()).
                    client(client).build();
        }
        return mRetrofit;
    }
    interface APIService {
        @Multipart
        @PUT("api/profilePic/{id}/")
        Call<ResponseBody> postImage(
                @Part MultipartBody.Part image,
                @Path("id") Integer id);

        @Multipart
        @POST("api/gathering/")
        Call<ResponseBody> createGathering(
                @Part("name") String name,
                @Part("start_datetime") String dateTime,
                @Part("created_by") Integer createdBy,
                @Part("restaurant") Integer restaurant);
    }
}
