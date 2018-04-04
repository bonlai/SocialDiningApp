package com.bonlai.socialdiningapp.network;

import com.bonlai.socialdiningapp.models.*;

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
import retrofit2.http.Query;

/**
 * Created by Bon Lai on 12/1/2018.
 */

public class APIclient {
    static Retrofit mRetrofit;
    static APIService mAPIService;
    private static OkHttpClient.Builder OKHttpBuilder=new OkHttpClient.Builder();
    private static Retrofit.Builder builder=new Retrofit.Builder().
            baseUrl("http://144.214.121.51/").
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
    }

    public static APIService getAPIService() {
        mAPIService=APIclient.retrofit().create(APIService.class);
        return mAPIService;
    }

    public interface APIService {
        @FormUrlEncoded
        @POST("api/rest-auth/login/")
        Call<Token> login(
                @Field("username") String username,
                @Field("password") String password);

        @FormUrlEncoded
        @POST("api/rest-auth/registration/")
        Call<Token> register(
                @Field("username") String username,
                @Field("password1") String password1,
                @Field("password2") String password2);

    }
}
