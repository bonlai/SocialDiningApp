package com.bonlai.socialdiningapp;

/**
 * Created by Bon Lai on 6/1/2018.
 */

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
/**
 * Created by adinugroho
 */

interface Service {
    @Multipart
    @PUT("api/profilePic/{id}/")
    Call<ResponseBody> postImage(
            @Part MultipartBody.Part image,
            @Path("id") Integer id);

    @POST("api/gathering/")
    Call<ResponseBody> createGathering(
            @Part MultipartBody.Part image,
            @Path("id") Integer id);
}