package com.bonlai.socialdiningapp;

import com.bonlai.socialdiningapp.models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import retrofit2.http.Query;

/**
 * Created by Bon Lai on 12/1/2018.
 */

public class APIclient {
    static Retrofit mRetrofit;
    static APIService mAPIService;
    private static OkHttpClient.Builder OKHttpBuilder = new OkHttpClient.Builder();
    private static Retrofit.Builder builder=new Retrofit.Builder().
            baseUrl("http://192.168.2.6:8000/").
            addConverterFactory(GsonConverterFactory.create());
    public static Retrofit retrofit() {
        if (mRetrofit == null) {

            //set log history
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //HTTP connection
            //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            OKHttpBuilder.addInterceptor(interceptor);
            //add authorization information to interceptor
            OkHttpClient client=OKHttpBuilder.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd hh:mm")
                    .create();

            mRetrofit = builder.client(client).build();
        }
        return mRetrofit;
    }

    public static void setToken(){
        OKHttpBuilder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Token "+Token.getToken().getKey()).build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client=OKHttpBuilder.build();
        mRetrofit = builder.client(client).build();
    }

    public static APIService getAPIService() {
        mAPIService=APIclient.retrofit().create(APIService.class);
        return mAPIService;
    }

    public interface APIService {
        @Multipart
        @PUT("api/user/{id}/profile/profile_pic_udate/")
        Call<ResponseBody> postImage(
                @Part MultipartBody.Part image,
                @Path("id") Integer id);

        @POST("api/gathering/")
        Call<ResponseBody> createGathering(
                @Body Gathering gathering);

        @GET("api/gathering/")
        Call<List<Gathering>> getGatheringList();

        @FormUrlEncoded
        @POST("api/rest-auth/login/")
        Call<Token> login(
                @Field("username") String username,
                @Field("password") String password);

        @GET("api/rest-auth/user/")
        Call<User> getMyDetail();

        @FormUrlEncoded
        @POST("api/rest-auth/registration/")
        Call<Token> register(
                @Field("username") String username,
                @Field("password1") String password1,
                @Field("password2") String password2);

        @FormUrlEncoded
        @POST("api/participate/")
        Call<ResponseBody> joinGathering(
                @Field("user") int userId,
                @Field("gathering") int gatheringId
        );

        @GET("api/restaurant/{id}/")
        Call<Restaurant> getRestaurantInfo(
                @Path("id") Integer id
        );

        @GET("api/restaurant/")
        Call<List<Restaurant>> getRestaurantList();

        @GET("api/user/{id}/profile/")
        Call<Profile> getProfile(
                @Path("id") Integer id
        );

        @FormUrlEncoded
        @PUT("api/user/{id}/profile/")
        Call<Profile> editProfile(
                @Path("id") Integer id,
                @Field("self_introduction") String bio
        );

        @GET("api/review_filter/")
        Call<List<Review>> getReview(
                @Query("restaurant") Integer restaurantId
        );
    }
}
