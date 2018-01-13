package com.bonlai.socialdiningapp;

import com.bonlai.socialdiningapp.models.Gathering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    static APIService mAPIService;
    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            //add authorization information to interceptor
            //set log history
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //HTTP connection
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd hh:mm")
                    .create();

            Retrofit.Builder builder=new Retrofit.Builder().
                    baseUrl("http://192.168.2.4:8000/").
                    addConverterFactory(GsonConverterFactory.create());

            mRetrofit = builder.client(client).build();
        }
        return mRetrofit;
    }

    public static APIService getAPIService() {
        mAPIService=APIclient.retrofit().create(APIService.class);
        return mAPIService;
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

        @POST("api/gathering/")
        Call<ResponseBody> createGatheringB(
                @Body Gathering gathering);

        @GET("api/gathering/")
        Call<List<Gathering>> getGatheringList();

        @FormUrlEncoded
        @POST("/rest-auth/login/")
        Call<ResponseBody> login(
                @Field("username") String username,
                @Field("password") String password);
    }
}
