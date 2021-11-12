package com.example.solarcommunity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoofActivity extends AppCompatActivity {

    // Button
    Button BUploadImage;
    ImageButton BSearchAdd;

    // Preview Image
    ImageView PreviewImage;

    // TextView
    TextView tv_cost, tv_preProfit, tv_num, tv_pv;
    EditText et_address;

    int REQUEST_CODE = 200;
    Uri selectedImage;
    String mediaPath;
    Bitmap bitmap;

    byte[] image_byte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roof);


        // register the UI widgets with their appropriate IDs
        BSearchAdd = findViewById(R.id.btn_search);
        PreviewImage = findViewById(R.id.PreviewImage);
        BUploadImage = findViewById(R.id.btn_uploadImage);

        // textview
        tv_preProfit = findViewById(R.id.tv_predict_profit);
        tv_num = findViewById(R.id.tv_num);
        tv_cost = findViewById(R.id.tv_cost);
        tv_pv = findViewById(R.id.tv_pv);

        et_address = findViewById(R.id.et_address);

        // handle the Choose Image button to trigger the image chooser function
        BSearchAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!et_address.getText().toString().equals(""))
                    getAddressImage();
                else Toast.makeText(RoofActivity.this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        BUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!et_address.getText().toString().equals("")) {
                    uploadImage();

                }else Toast.makeText(RoofActivity.this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setTextView(String number){
        int num = Integer.parseInt(number);
        int pv_num = num * 6;
        int price = 11 * pv_num;
        double predict_amount = (double)pv_num * 1.37;
        // price = Math.round(price*100)/100.0;
        predict_amount = Math.round(predict_amount*100)/100.0;
        double cost = predict_amount * 71.873 *30;
        cost = Math.round(cost*100)/100.0;
        tv_num.setText(pv_num+" 개");
        tv_cost.setText(price+" 만원");
        tv_pv.setText(predict_amount + " KWh");
        tv_preProfit.setText((int)cost+" 원");
    }

    // get Image of the input address
    private void getAddressImage(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoAPI.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        DjangoAPI postApi= retrofit.create(DjangoAPI.class);
        Call<ResponseBody> call = postApi.requestImage(et_address.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if(!response.isSuccessful()){
                    Log.e("연결이 비정상적 : ", "error code : " + response.code());
                    return;
                }

                ResponseBody body = response.body();

                String result = null;

                try {
                    result = body.string();

                    byte[] byteArray = Base64.decode(result, Base64.DEFAULT);

                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    PreviewImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.toString());
            }
        });

    }

    // Django 서버로 이미지 전송
    private void uploadImage(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoAPI.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        DjangoAPI postApi= retrofit.create(DjangoAPI.class);

        Call<ResponseBody> call = postApi.uploadFile(et_address.getText().toString());
        //Call<ResponseBody> call = postApi.getImg();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if(!response.isSuccessful()){
                    Log.e("연결이 비정상적 : ", "error code : " + response.code());
                    return;
                }

                ResponseBody body = response.body();

                String result = null;

                try {
                    InputStream is = body.byteStream();
                    int fileSize = is.available();

                    byte[] buffer = new byte[fileSize];
                    is.read(buffer);
                    is.close();

                    result = new String(buffer, "UTF-8");

                    JSONObject jsonObject = new JSONObject(result);
                    Object img = jsonObject.get("img");
                    Object number = jsonObject.get("number");

                    Log.d("number", number.toString());

                    byte[] byteArray = Base64.decode(img.toString(), Base64.DEFAULT);

                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    PreviewImage.setImageBitmap(bitmap);
                    tv_num.setText(number.toString()+" 개");
                    setTextView(number.toString());

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.toString());
            }
        });

    }

}