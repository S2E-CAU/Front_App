package com.example.solarcommunity;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DjangoAPI {
    String DJANGO_SITE="http://10.0.2.2:8000";

    @Multipart
    @POST("api/map")
    Call <ResponseBody> uploadFile(@Part MultipartBody.Part file);

    
}