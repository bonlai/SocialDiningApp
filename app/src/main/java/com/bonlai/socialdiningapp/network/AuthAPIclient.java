package com.bonlai.socialdiningapp.network;

import android.util.Log;

import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.Interest;
import com.bonlai.socialdiningapp.models.MapMarkerInfo;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.models.Review;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.models.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Bon Lai on 12/1/2018.
 */

public class AuthAPIclient {
    static Retrofit mRetrofit;
    static APIService mAPIService;
    private static OkHttpClient.Builder OKHttpBuilder=new OkHttpClient.Builder();
    private static Retrofit.Builder builder=new Retrofit.Builder().
            baseUrl("http://192.168.2.4:8000/").
            addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            setOKHttpBuilder();
            OkHttpClient client=OKHttpBuilder.build();
            mRetrofit = builder.client(client).build();
        }
        return mRetrofit;
    }

    private static void setOKHttpBuilder(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OKHttpBuilder.addInterceptor(interceptor);
        OKHttpBuilder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
    }

    public static void setAuthToken(){
        //Log.d("set token","RETROFIT");
        OKHttpBuilder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                //Log.d("RETROFIT","add inter");
                Request request = chain.request().newBuilder().addHeader("Authorization", "Token "+Token.getToken().getKey()).build();
                //Log.d("RETROFIT",Token.getToken().getKey());
                return chain.proceed(request);
            }
        });
    }

    public static APIService getAPIService() {
        mAPIService= AuthAPIclient.retrofit().create(APIService.class);
        return mAPIService;
    }

    public interface APIService {
        @GET("api/rest-auth/user/")
        Call<User> getMyDetail();

        @GET("api/user/")
        Call<List<User>> getOthersDetail(
                @Query("id") int userId
        );

        @GET("api/user/{id}/profile/")
        Call<Profile> getProfile(
                @Path("id") Integer id
        );

        @PUT("api/user/{id}/profile/")
        Call<Profile> editProfile(
                @Path("id") Integer id,
                @Body Profile profile
        );

        @FormUrlEncoded
        @PUT("api/user/{id}/lat_long/")
        Call<ResponseBody> returnLatLong(
                @Path("id") Integer id,
                @Field("latitude") double latitude,
                @Field("longitude") double longitude
        );


        @Multipart
        @PUT("api/user/{id}/profile/profile_pic_udate/")
        Call<ResponseBody> postImage(
                @Part MultipartBody.Part image,
                @Path("id") Integer id);

        @GET("api/user/{id}/gathering/")
        Call<List<Gathering>> getMyGatheringList(
                @Path("id") Integer id);

        @GET("api/interest/")
        Call<List<Interest>> getInterestList();

        @FormUrlEncoded
        @POST("api/interest/")
        Call<ResponseBody> postInterest(
                @Field("name") String bio
        );

        @DELETE("api/user/{id}/interest/")
        Call<ResponseBody> clearInterest(
                @Path("id") Integer id
        );

        @GET("api/interest/")
        Call<List<Interest>> getMyInterestList(
                @Query("user") Integer id
        );

        @POST("api/gathering/")
        Call<ResponseBody> createGathering(
                @Body Gathering gathering);

        @GET("api/gathering/")
        Call<List<Gathering>> getGatheringList(
                @Query("page") Integer pageNum,
                @QueryMap Map<String, String> options
        );

        @GET("api/gathering/location/")
        Call<List<MapMarkerInfo>> getGatheringLocationList();

        @GET("api/gathering/{id}/")
        Call<Gathering> getGatheringDetail(
                @Path("id") Integer id
        );

        @PUT("api/gathering/{id}/")
        Call<Gathering> putGathering(
                @Path("id") Integer id,
                @Body Gathering gathering);

        @FormUrlEncoded
        @PATCH("api/gathering/{id}/")
        Call<Gathering> startGathering(
                @Path("id") Integer id,
                @Field("is_start") boolean isStart
        );

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
        Call<List<Restaurant>> getRestaurantList(
                @Query("page") Integer pageNum,
                @Query("search") String query
        );


        @GET("api/review/")
        Call<List<Review>> getReview(
                @Query("restaurant") Integer restaurantId
        );

        @FormUrlEncoded
        @POST("api/review/")
        Call<ResponseBody> postReview(
                @Field("comment") String comment,
                @Field("rating") int rating,
                @Field("restaurant") int restaurantId
        );
    }
}
