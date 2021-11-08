package com.example.solarcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
    LineChart costChart;
    Button btn_day, btn_enter;
    TextView tv_profit;

    ArrayList<SolarCost> solarCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        btn_day = findViewById(R.id.btn_day);
        btn_enter = findViewById(R.id.btn_enter);
        tv_profit = findViewById(R.id.tv_profit);

        // list 초기화
        solarCost = new ArrayList<>();

        solarCost.add(new SolarCost("1", "59148"));
        solarCost.add(new SolarCost("2", "57213"));
        solarCost.add(new SolarCost("3", "56020"));
        solarCost.add(new SolarCost("4", "55383"));
        solarCost.add(new SolarCost("5", "55512"));
        solarCost.add(new SolarCost("6", "56538"));
        solarCost.add(new SolarCost("7", "58419"));
        solarCost.add(new SolarCost("8", "61319"));
        solarCost.add(new SolarCost("9", "64936"));
        solarCost.add(new SolarCost("10", "66403"));

        solarCost.add(new SolarCost("11", "67042"));
        solarCost.add(new SolarCost("12", "66658"));
        solarCost.add(new SolarCost("13", "66540"));
        solarCost.add(new SolarCost("14", "68110"));
        solarCost.add(new SolarCost("15", "69384"));
        solarCost.add(new SolarCost("16", "70692"));
        solarCost.add(new SolarCost("17", "71784"));
        solarCost.add(new SolarCost("18", "71873"));
        solarCost.add(new SolarCost("19", "71528"));
        solarCost.add(new SolarCost("20", "71006"));

        solarCost.add(new SolarCost("21", "69358"));
        solarCost.add(new SolarCost("22", "66006"));
        solarCost.add(new SolarCost("23", "62740"));
        solarCost.add(new SolarCost("24", "60348"));


        // graph
        costChart = findViewById(R.id.costChart);

        btn_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spCostChart();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price = 1.8 * 60348;
                price = Math.round(price*100)/100.0;
                tv_profit.setText(Double.toString(price)+" 원");
            }
        });
    }


    private List<Entry> solarValue(){
        List<Entry> sps = new ArrayList<>();

        for (int i=0; i<solarCost.size();i++) {
            sps.add(new Entry((float)i, Integer.parseInt(solarCost.get(i).getCost())));
        }

        return sps;
    }


    private void spCostChart(){
        LineDataSet score = new LineDataSet(solarValue(),"태양광 발전량 예측 가격");
        score.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(score);
        LineData data = new LineData(dataSets);
        costChart.setData(data);
        costChart.invalidate(); // refresh

        // the labels that should be drawn on the XAxis
        String[] labels = new String[solarCost.size()];
        for(int i=0; i<solarCost.size();i++){
            labels[i] = solarCost.get(i).getTime();
        }

        XAxis xAxis = costChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        Legend legend = costChart.getLegend();
        legend.setEnabled(false);
        Description description = costChart.getDescription();
        description.setEnabled(false);
    }


}
