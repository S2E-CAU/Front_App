package com.example.solarcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PriceActivity extends AppCompatActivity {
    Button btn_now, btn_1, btn_2, btn_24, btn_sale;
    TextView tv_profit, tv_price;
    double price;
    ArrayList<SaleData> saleDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        btn_now = findViewById(R.id.btn_now);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_24 = findViewById(R.id.btn_24);
        btn_sale = findViewById(R.id.btn_sale);
        tv_profit = findViewById(R.id.tv_profit);
        tv_price = findViewById(R.id.tv_solarPrice);

        InitializeUserSaleData();

        ListView listView = (ListView)findViewById(R.id.listview);
        final SaleAdapter saleAdapter = new SaleAdapter(this,saleDataList);

        listView.setAdapter(saleAdapter);


        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("현재 예상 판매가: 71.78 원/KWh");
                price = 180 * 71.78;
                price = Math.round(price*100)/100.0;
                tv_profit.setText(price+" 원");
                Log.d("HELLO", "Button now Clicked");
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("1시간 뒤 예상 판매가: 71.87 원/KWh");
                price = 180 * 71.87;
                price = Math.round(price*100)/100.0;
                tv_profit.setText(price+" 원");
                Log.d("HELLO", "Button1 Clicked");
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("2시간 뒤 예상 판매가: 71.52 원/KWh");
                price = 180 * 71.52;
                price = Math.round(price*100)/100.0;
                tv_profit.setText(price+" 원");
                Log.d("HELLO", "Button2 Clicked");
            }
        });

        btn_24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("내일 예상 판매가: 72.67 원/KWh");
                price = 180 * 72.67;
                price = Math.round(price*100)/100.0;
                tv_profit.setText(price+" 원");
                Log.d("HELLO", "Button24 Clicked");
            }
        });

        btn_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PriceActivity.this,"판매 화면으로 이동합니다.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void InitializeUserSaleData(){
        saleDataList = new ArrayList<SaleData>();

        saleDataList.add(new SaleData("unknown1",50));
        saleDataList.add(new SaleData("unknown2",120));
        saleDataList.add(new SaleData("unknown3",30));
        saleDataList.add(new SaleData("unknown4",170));
    }

}
