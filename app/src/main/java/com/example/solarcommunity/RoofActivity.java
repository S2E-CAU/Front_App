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
    Button BSelectImage, BUploadImage;

    // Preview Image
    ImageView PreviewImage;

    // TextView
    TextView tv_cost, tv_area, tv_num;

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
        BSelectImage = findViewById(R.id.btn_selectImage);
        PreviewImage = findViewById(R.id.PreviewImage);
        BUploadImage = findViewById(R.id.btn_uploadImage);

        // textview
        tv_area = findViewById(R.id.tv_area);
        tv_num = findViewById(R.id.tv_num);
        tv_cost = findViewById(R.id.tv_cost);

        // handle the Choose Image button to trigger the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserWithMediaStore();
            }
        });

        BUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { uploadImage(image_byte); }
        });

    }

    // load image using MediaStore
    void imageChooserWithMediaStore(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    // this function is triggered when user elects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // load image using MediaStore
        if(requestCode== REQUEST_CODE && resultCode==RESULT_OK && data!=null) {

            selectedImage = data.getData();
            Uri photoUri = data.getData();
            bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // load image
            PreviewImage.setImageBitmap(bitmap);

            // 사진 경로
            Cursor cursor = getContentResolver().query(Uri.parse(selectedImage.toString()), null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

            //Log.d("경로 확인 >> ", "$selectedImg  /  $absolutePath");
            Log.d("경로 확인 >>", mediaPath);
            // /storage/emulated/0/DCIM/Camera/20210905_184903.jpg

            try {
                InputStream is = getContentResolver().openInputStream(data.getData());

                Log.d("이미지 로드","업로드 시작");
                image_byte = getBytes(is);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show();
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        Log.d("bytes_image", byteBuff.toByteArray().toString());

        return byteBuff.toByteArray();
    }



    // Django 서버로 이미지 전송
    private void uploadImage(byte[] imageBytes){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
//                    .addNetworkInterceptor(interceptor)
                .build();
//                    .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT,ConnectionSpec.MODERN_TLS))

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoAPI.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        DjangoAPI postApi= retrofit.create(DjangoAPI.class);

        RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse("image/jpg"));

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file","image.jpg",requestBody);


        Call<ResponseBody> call = postApi.uploadFile(fileToUpload);
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

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.toString());
                //Log.d("fail", "fail");
            }
        });

    }

}