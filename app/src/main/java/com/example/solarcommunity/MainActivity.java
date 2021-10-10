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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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


public class MainActivity extends AppCompatActivity {

    Button btn_roof, btn_price;
    ImageButton btn_update;
    LineChart lineChart;

    ArrayList<SolarPower> solarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_price = findViewById(R.id.btn_solarPrice);
        btn_roof = findViewById(R.id.btn_roof);
        btn_update = findViewById(R.id.btn_update);

        // list 초기화
        solarList = new ArrayList<>();

        // graph
        lineChart = findViewById(R.id.line_solar);

        btn_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PriceActivity.class);
                startActivity(intent);
            }
        });

        btn_roof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoofActivity.class);
                startActivity(intent);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendpost();
            }
        });



    }

    private void sendpost(){
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

        Call<ResponseBody> call = postApi.getSolarPower();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
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

                    jsonParsing(result);

                    spLineChart();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Log.d("fail", t.toString());
            }
        });

    }


    private void jsonParsing(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("Solar");

            for(int i=0; i<jsonArray.length();i++){
                JSONObject solarObject = jsonArray.getJSONObject(i);

                SolarPower sp = new SolarPower();

                sp.setDay(solarObject.getString("Time"));
                sp.setValue(solarObject.getString("Value"));
                solarList.add(sp);

                Log.d("Day",solarObject.getString("Time"));
                Log.d("Value",solarObject.getString("Value"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Entry> solarValue(){
        List<Entry> sps = new ArrayList<>();

        for (int i=0; i<solarList.size();i++) {
            sps.add(new Entry((float)i, Float.parseFloat(solarList.get(i).getValue())));
        }

        return sps;
    }


    private void spLineChart(){
        LineDataSet score = new LineDataSet(solarValue(),"태양광발전량");
        score.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(score);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate(); // refresh

        // the labels that should be drawn on the XAxis
        String[] labels = new String[solarList.size()];
        for(int i=0; i<solarList.size();i++){
            labels[i] = solarList.get(i).getDay().substring(11,16);
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        Description description = lineChart.getDescription();
        description.setEnabled(false);
    }


}
