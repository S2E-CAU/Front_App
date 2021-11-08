package com.example.solarcommunity;

public class SolarCost {

    private String cost;
    private String time;

    public SolarCost(String time, String cost){
        this.cost = cost;
        this.time = time;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
