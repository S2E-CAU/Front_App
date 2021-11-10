package com.example.solarcommunity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SaleAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<SaleData> data;

    public SaleAdapter(Context mContext,ArrayList<SaleData> data) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public SaleData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.list_view ,null);

        TextView userName = (TextView)view.findViewById(R.id.user);
        TextView tv_amount = (TextView)view.findViewById(R.id.saleAmount);

        userName.setText(data.get(position).getName());
        tv_amount.setText(data.get(position).getAmount()+" kWh");

        return view;
    }
}
