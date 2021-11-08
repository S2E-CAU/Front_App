package com.example.solarcommunity;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DjangoAPI {
    String DJANGO_SITE="http://10.0.2.2:8000/";
    //String DJANGO_SITE="http://ec2-13-125-127-153.ap-northeast-2.compute.amazonaws.com";

    @POST("api/map")
    Call <ResponseBody> uploadFile(@Body String address);
    
    @POST("api/solar")
    Call <ResponseBody> getSolarPower();

    @POST("api/test")
    Call <ResponseBody> getImg();

    @POST("api/address")
    Call <ResponseBody> requestImage(@Body String address);
}